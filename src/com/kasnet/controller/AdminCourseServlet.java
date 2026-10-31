package com.kasnet.controller;

import com.kasnet.dao.CourseDAO;
import com.kasnet.dao.LectureDAO;
import com.kasnet.model.Course;
import com.kasnet.model.Lecture;
import com.kasnet.util.CloudinaryUtil;
import com.kasnet.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * AdminCourseServlet — Handles Course & Lecture CRUD management, video uploads,
 * and Cloudinary asset synchronization for administrators.
 *
 * Routes:
 *   GET  /admin/courses           → Lists all courses with lecture/enrollment counts
 *   GET  /admin/courses?manage=1  → Lecture management view for Course #1
 *   POST /admin/courses           → Creates/updates/deletes courses & lectures, video uploads
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,       // 2MB
        maxFileSize = 1024L * 1024L * 500L,        // 500MB max per video
        maxRequestSize = 1024L * 1024L * 510L      // 510MB total request
)
public class AdminCourseServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminCourseServlet.class);

    private final CourseDAO courseDAO = new CourseDAO();
    private final LectureDAO lectureDAO = new LectureDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireLogin(req, resp)) return;

        String role = SessionUtil.getUserRole(req);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/views/unauthorized.jsp");
            return;
        }

        String manageCourseIdParam = req.getParameter("manage");
        if (manageCourseIdParam != null && !manageCourseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(manageCourseIdParam);
                Course course = courseDAO.getCourseById(courseId);
                if (course == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/courses?err=not_found");
                    return;
                }
                List<Lecture> lectures = lectureDAO.getAllLecturesByCourse(courseId);

                req.setAttribute("course", course);
                req.setAttribute("lectures", lectures);
                req.setAttribute("viewMode", "manage_lectures");
                req.getRequestDispatcher("/views/admin-courses.jsp").forward(req, resp);
                return;
            } catch (NumberFormatException ignored) {}
        }

        // Default: list all courses
        List<Course> courses = courseDAO.getAllCourses();
        req.setAttribute("courseList", courses);
        req.setAttribute("viewMode", "list_courses");
        req.getRequestDispatcher("/views/admin-courses.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireLogin(req, resp)) return;

        String role = SessionUtil.getUserRole(req);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/views/unauthorized.jsp");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "";

        switch (action.toLowerCase()) {
            case "createcourse":
                handleCreateCourse(req, resp);
                break;
            case "updatecourse":
                handleUpdateCourse(req, resp);
                break;
            case "deletecourse":
                handleDeleteCourse(req, resp);
                break;
            case "createlecture":
                handleCreateLecture(req, resp);
                break;
            case "updatelecture":
                handleUpdateLecture(req, resp);
                break;
            case "deletelecture":
                handleDeleteLecture(req, resp);
                break;
            case "uploadvideo":
                handleUploadVideo(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/admin/courses");
                break;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Course Handlers
    // ─────────────────────────────────────────────────────────────────────────
    private void handleCreateCourse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String instructorName = req.getParameter("instructorName");
        String category = req.getParameter("category");
        String difficulty = req.getParameter("difficulty");
        String status = req.getParameter("status");
        String thumbnailUrl = req.getParameter("thumbnailUrl");

        int durationWeeks = 6;
        try {
            durationWeeks = Integer.parseInt(req.getParameter("durationWeeks"));
        } catch (Exception ignored) {}

        Course c = new Course();
        c.setTitle(title != null ? title.trim() : "Untitled Course");
        c.setDescription(description);
        c.setInstructorName(instructorName != null ? instructorName.trim() : "Instructor");
        c.setCategory(category != null ? category.trim() : "Engineering");
        c.setDifficulty(difficulty != null ? difficulty.trim() : "Beginner");
        c.setStatus(status != null ? status.trim() : "PUBLISHED");
        c.setThumbnailUrl(thumbnailUrl);
        c.setDurationWeeks(durationWeeks);
        c.setTotalLessons(0);
        c.setSlug(c.generateSlug());

        int id = courseDAO.createCourse(c);
        resp.sendRedirect(req.getContextPath() + "/admin/courses?success=course_created&id=" + id);
    }

    private void handleUpdateCourse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            Course c = courseDAO.getCourseById(courseId);
            if (c != null) {
                c.setTitle(req.getParameter("title"));
                c.setDescription(req.getParameter("description"));
                c.setInstructorName(req.getParameter("instructorName"));
                c.setCategory(req.getParameter("category"));
                c.setDifficulty(req.getParameter("difficulty"));
                c.setStatus(req.getParameter("status"));
                c.setThumbnailUrl(req.getParameter("thumbnailUrl"));
                try {
                    c.setDurationWeeks(Integer.parseInt(req.getParameter("durationWeeks")));
                } catch (Exception ignored) {}

                courseDAO.updateCourse(c);
                resp.sendRedirect(req.getContextPath() + "/admin/courses?success=course_updated&manage=" + courseId);
                return;
            }
        } catch (Exception ignored) {}
        resp.sendRedirect(req.getContextPath() + "/admin/courses?err=update_failed");
    }

    private void handleDeleteCourse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            courseDAO.deleteCourse(courseId);
            resp.sendRedirect(req.getContextPath() + "/admin/courses?success=course_deleted");
            return;
        } catch (Exception ignored) {}
        resp.sendRedirect(req.getContextPath() + "/admin/courses?err=delete_failed");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lecture Handlers
    // ─────────────────────────────────────────────────────────────────────────
    private void handleCreateLecture(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String cloudinaryPublicId = req.getParameter("cloudinaryPublicId");
            String status = req.getParameter("status");
            boolean isPreview = "true".equalsIgnoreCase(req.getParameter("isPreview")) || "1".equals(req.getParameter("isPreview"));

            int lectureNumber = 1;
            try { lectureNumber = Integer.parseInt(req.getParameter("lectureNumber")); } catch (Exception ignored) {}

            int durationSeconds = 0;
            try { durationSeconds = Integer.parseInt(req.getParameter("durationSeconds")); } catch (Exception ignored) {}

            Lecture l = new Lecture();
            l.setCourseId(courseId);
            l.setTitle(title != null ? title.trim() : "New Lecture");
            l.setDescription(description);
            l.setLectureNumber(lectureNumber);
            l.setCloudinaryPublicId(cloudinaryPublicId);
            l.setDurationSeconds(durationSeconds);
            l.setPreview(isPreview);
            l.setStatus(status != null ? status.trim() : "PUBLISHED");

            lectureDAO.createLecture(l);
            resp.sendRedirect(req.getContextPath() + "/admin/courses?manage=" + courseId + "&success=lecture_created");
            return;
        } catch (Exception ignored) {}
        resp.sendRedirect(req.getContextPath() + "/admin/courses?err=lecture_create_failed");
    }

    private void handleUpdateLecture(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int lectureId = Integer.parseInt(req.getParameter("lectureId"));
            Lecture l = lectureDAO.getLectureById(lectureId);
            if (l != null) {
                l.setTitle(req.getParameter("title"));
                l.setDescription(req.getParameter("description"));
                l.setCloudinaryPublicId(req.getParameter("cloudinaryPublicId"));
                l.setStatus(req.getParameter("status"));
                l.setPreview("true".equalsIgnoreCase(req.getParameter("isPreview")) || "1".equals(req.getParameter("isPreview")));

                try { l.setLectureNumber(Integer.parseInt(req.getParameter("lectureNumber"))); } catch (Exception ignored) {}
                try { l.setDurationSeconds(Integer.parseInt(req.getParameter("durationSeconds"))); } catch (Exception ignored) {}

                lectureDAO.updateLecture(l);
                resp.sendRedirect(req.getContextPath() + "/admin/courses?manage=" + l.getCourseId() + "&success=lecture_updated");
                return;
            }
        } catch (Exception ignored) {}
        resp.sendRedirect(req.getContextPath() + "/admin/courses?err=lecture_update_failed");
    }

    private void handleDeleteLecture(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int lectureId = Integer.parseInt(req.getParameter("lectureId"));
            Lecture l = lectureDAO.getLectureById(lectureId);
            int courseId = (l != null) ? l.getCourseId() : 0;

            if (l != null && l.getCloudinaryPublicId() != null) {
                // Delete asset from Cloudinary
                CloudinaryUtil.deleteVideo(l.getCloudinaryPublicId());
            }

            lectureDAO.deleteLecture(lectureId);
            resp.sendRedirect(req.getContextPath() + "/admin/courses?manage=" + courseId + "&success=lecture_deleted");
            return;
        } catch (Exception ignored) {}
        resp.sendRedirect(req.getContextPath() + "/admin/courses?err=delete_failed");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Video Upload (Direct file upload to Cloudinary)
    // ─────────────────────────────────────────────────────────────────────────
    private void handleUploadVideo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int lectureId = 0;
        int courseId = 0;
        try {
            lectureId = Integer.parseInt(req.getParameter("lectureId"));
            courseId = Integer.parseInt(req.getParameter("courseId"));

            Part videoPart = req.getPart("videoFile");
            if (videoPart == null || videoPart.getSize() == 0) {
                resp.sendRedirect(req.getContextPath() + "/admin/courses?manage=" + courseId + "&err=no_file");
                return;
            }

            String fileName = extractFileName(videoPart);
            logger.info("Uploading lecture video: {} (size: {} bytes)", fileName, videoPart.getSize());

            // Write temporary file
            File tempFile = File.createTempFile("kasnet_vid_", ".mp4");
            try (InputStream in = videoPart.getInputStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Cloudinary publicId path
            String publicId = "kasanet/lectures/course_" + courseId + "/lecture_" + lectureId + "_" + System.currentTimeMillis();

            Map<String, Object> uploadResult = CloudinaryUtil.uploadVideo(tempFile, publicId);
            tempFile.delete();

            String uploadedPublicId = (String) uploadResult.get("public_id");
            Number durationNum = (Number) uploadResult.get("duration");
            int durationSeconds = durationNum != null ? (int) Math.round(durationNum.doubleValue()) : 0;

            // Update database
            lectureDAO.updateLectureVideo(lectureId, uploadedPublicId, durationSeconds);

            resp.sendRedirect(req.getContextPath() + "/admin/courses?manage=" + courseId + "&success=video_uploaded");
            return;

        } catch (Exception e) {
            logger.error("Error during video upload for lecture: {}", lectureId, e);
            resp.sendRedirect(req.getContextPath() + "/admin/courses?manage=" + courseId + "&err=upload_error");
        }
    }

    private String extractFileName(Part part) {
        String cdHeader = part.getHeader("content-disposition");
        if (cdHeader != null) {
            for (String cd : cdHeader.split(";")) {
                if (cd.trim().startsWith("filename")) {
                    String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                    return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
                }
            }
        }
        return "uploaded_video.mp4";
    }
}
