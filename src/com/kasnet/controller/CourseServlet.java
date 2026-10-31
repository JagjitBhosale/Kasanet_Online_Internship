package com.kasnet.controller;

import com.kasnet.dao.CourseDAO;
import com.kasnet.dao.EnrollmentDAO;
import com.kasnet.dao.LectureDAO;
import com.kasnet.dao.VideoAccessLogDAO;
import com.kasnet.model.Course;
import com.kasnet.model.Enrollment;
import com.kasnet.model.Lecture;
import com.kasnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * CourseServlet — handles course catalog, course detail, student enrollment,
 * and lecture player page.
 *
 * Routes:
 *   GET  /courses                                     → course catalog (all published courses)
 *   GET  /courses?id=1                                → course detail page with lecture list
 *   GET  /courses?view=player&courseId=1&lectureId=11 → secure video lecture player
 *   POST /courses?action=enroll                       → enroll the logged-in user
 */
public class CourseServlet extends HttpServlet {

    private final CourseDAO courseDAO = new CourseDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final LectureDAO lectureDAO = new LectureDAO();
    private final VideoAccessLogDAO videoAccessLogDAO = new VideoAccessLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String view = req.getParameter("view");
        String courseIdParam = req.getParameter("id");
        if (courseIdParam == null) {
            courseIdParam = req.getParameter("courseId");
        }

        // ───────── 1. Lecture Player View ─────────
        if ("player".equalsIgnoreCase(view) || req.getParameter("lectureId") != null) {
            handlePlayerView(req, resp, userId, courseIdParam);
            return;
        }

        // ───────── 2. Course Detail View ─────────
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            handleCourseDetailView(req, resp, userId, courseIdParam);
            return;
        }

        // ───────── 3. Course Catalog View ─────────
        handleCatalogView(req, resp, userId);
    }

    private void handlePlayerView(HttpServletRequest req, HttpServletResponse resp,
                                  int userId, String courseIdParam) throws ServletException, IOException {
        try {
            int courseId = 0;
            String lectureIdParam = req.getParameter("lectureId");
            if (courseIdParam != null && !courseIdParam.trim().isEmpty()) {
                try {
                    courseId = Integer.parseInt(courseIdParam.trim());
                } catch (NumberFormatException ignored) {}
            }

            // If courseId is not provided, look up the courseId from the lecture
            if (courseId <= 0 && lectureIdParam != null && !lectureIdParam.trim().isEmpty()) {
                try {
                    Lecture targetLec = lectureDAO.getLectureById(Integer.parseInt(lectureIdParam.trim()));
                    if (targetLec != null && targetLec.getCourseId() > 0) {
                        courseId = targetLec.getCourseId();
                    }
                } catch (Exception ignored) {}
            }

            // If still 0, pick the first published course
            if (courseId <= 0) {
                List<Course> published = courseDAO.getPublishedCourses(userId);
                if (!published.isEmpty()) {
                    courseId = published.get(0).getCourseId();
                } else {
                    resp.sendRedirect(req.getContextPath() + "/courses");
                    return;
                }
            }

            Course course = courseDAO.getCourseById(courseId);
            if (course == null || !"PUBLISHED".equalsIgnoreCase(course.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/courses");
                return;
            }

            boolean enrolled = enrollmentDAO.isEnrolled(userId, courseId);
            String userRole = SessionUtil.getUserRole(req);
            boolean isAdmin = "ADMIN".equalsIgnoreCase(userRole);

            // Free enrollment system: auto-enroll student on lecture access so learning is never blocked
            if (!isAdmin && !enrolled) {
                enrollmentDAO.enroll(userId, courseId);
                enrolled = true;
            }
            course.setEnrolled(enrolled);

            List<Lecture> lectures = lectureDAO.getLecturesByCourseWithProgress(courseId, userId);
            if (lectures.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/courses?id=" + courseId + "&empty=1");
                return;
            }

            // Identify active lecture
            Lecture activeLecture = null;
            if (lectureIdParam != null && !lectureIdParam.isEmpty()) {
                int targetLecId = Integer.parseInt(lectureIdParam);
                for (Lecture l : lectures) {
                    if (l.getLectureId() == targetLecId) {
                        activeLecture = l;
                        break;
                    }
                }
            }

            // If not found or not specified, pick first incomplete, or first lecture
            if (activeLecture == null) {
                for (Lecture l : lectures) {
                    if (!l.isCompleted()) {
                        activeLecture = l;
                        break;
                    }
                }
                if (activeLecture == null) {
                    activeLecture = lectures.get(0);
                }
            }

            // Calculate completed count
            int completedCount = 0;
            for (Lecture l : lectures) {
                if (l.isCompleted()) completedCount++;
            }
            int progressPercent = (completedCount * 100) / lectures.size();

            // Next and previous lectures
            Lecture prevLecture = null;
            Lecture nextLecture = null;
            int currentIndex = lectures.indexOf(activeLecture);
            if (currentIndex > 0) {
                prevLecture = lectures.get(currentIndex - 1);
            }
            if (currentIndex >= 0 && currentIndex < lectures.size() - 1) {
                nextLecture = lectures.get(currentIndex + 1);
            }

            req.setAttribute("course", course);
            req.setAttribute("lectures", lectures);
            req.setAttribute("activeLecture", activeLecture);
            req.setAttribute("prevLecture", prevLecture);
            req.setAttribute("nextLecture", nextLecture);
            req.setAttribute("completedCount", completedCount);
            req.setAttribute("totalLectures", lectures.size());
            req.setAttribute("progressPercent", progressPercent);
            req.setAttribute("enrolled", enrolled);

            req.getRequestDispatcher("/views/lecture-player.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/courses");
        }
    }

    private void handleCourseDetailView(HttpServletRequest req, HttpServletResponse resp,
                                        int userId, String courseIdParam) throws ServletException, IOException {
        try {
            int courseId = Integer.parseInt(courseIdParam);
            Course course = courseDAO.getCourseById(courseId);
            if (course == null || !"PUBLISHED".equalsIgnoreCase(course.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/courses");
                return;
            }

            boolean enrolled = enrollmentDAO.isEnrolled(userId, courseId);
            course.setEnrolled(enrolled);

            if (enrolled) {
                Enrollment enrollment = enrollmentDAO.getEnrollment(userId, courseId);
                if (enrollment != null) {
                    course.setUserProgress(enrollment.getProgressPercentage());
                    course.setUserCompletedLessons(enrollment.getCompletedLessons());
                }
            }

            List<Lecture> lectures = lectureDAO.getLecturesByCourseWithProgress(courseId, userId);
            course.setLectureCount(lectures.size());
            course.setEnrollmentCount(courseDAO.countEnrollmentsByCourse(courseId));

            // Find first incomplete lecture for "Continue Learning"
            Lecture nextIncomplete = null;
            for (Lecture l : lectures) {
                if (!l.isCompleted()) {
                    nextIncomplete = l;
                    break;
                }
            }
            if (nextIncomplete == null && !lectures.isEmpty()) {
                nextIncomplete = lectures.get(0);
            }

            req.setAttribute("course", course);
            req.setAttribute("lectures", lectures);
            req.setAttribute("nextIncomplete", nextIncomplete);
            req.setAttribute("viewMode", "detail");
            req.getRequestDispatcher("/views/courses.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/courses");
        }
    }

    private void handleCatalogView(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        List<Course> courses = courseDAO.getPublishedCourses(userId);
        int enrolledCount = enrollmentDAO.countEnrollments(userId);

        req.setAttribute("courseList", courses);
        req.setAttribute("enrolledCount", enrolledCount);
        req.setAttribute("viewMode", "catalog");
        req.getRequestDispatcher("/views/courses.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireLogin(req, resp)) return;

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String courseIdParam = req.getParameter("courseId");

        if ("enroll".equalsIgnoreCase(action) && courseIdParam != null) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                Course course = courseDAO.getCourseById(courseId);
                if (course != null && "PUBLISHED".equalsIgnoreCase(course.getStatus())) {
                    enrollmentDAO.enroll(userId, courseId);
                }
                resp.sendRedirect(req.getContextPath() + "/courses?id=" + courseId + "&enrolled=1");
                return;
            } catch (NumberFormatException ignored) {}
        }

        resp.sendRedirect(req.getContextPath() + "/courses");
    }
}
