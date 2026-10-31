package com.kasnet.controller;

import com.kasnet.dao.CourseDAO;
import com.kasnet.dao.EnrollmentDAO;
import com.kasnet.dao.LectureDAO;
import com.kasnet.dao.VideoAccessLogDAO;
import com.kasnet.model.Course;
import com.kasnet.model.Enrollment;
import com.kasnet.model.Lecture;
import com.kasnet.util.CloudinaryUtil;
import com.kasnet.util.SessionUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * LectureApiServlet — REST API endpoints for the Course Video Lecture System.
 *
 * Supported Routes:
 *   GET  /api/lectures/{id}/video     → Generates 5-minute signed Cloudinary video stream URL
 *   POST /api/lectures/{id}/progress  → Auto-saves watch position (upsert with GREATEST logic)
 *   GET  /api/lectures/{id}           → Lecture metadata & user progress (NO video URL)
 *   GET  /api/courses/{id}/progress   → Course-level progress breakdown
 */
public class LectureApiServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LectureApiServlet.class);

    private final CourseDAO courseDAO = new CourseDAO();
    private final LectureDAO lectureDAO = new LectureDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final VideoAccessLogDAO videoAccessLogDAO = new VideoAccessLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo(); // e.g. "/11/video" or "/11"
        if (pathInfo == null) pathInfo = "";
        String[] parts = pathInfo.split("/");

        // Route: GET /api/lectures/{id}/video
        if (parts.length >= 3 && "video".equalsIgnoreCase(parts[2])) {
            handleVideoUrlRequest(req, resp, parts[1]);
            return;
        }

        // Route: GET /api/lectures/{id}
        if (parts.length >= 2 && !parts[1].isEmpty()) {
            handleLectureDetailRequest(req, resp, parts[1]);
            return;
        }

        // Query param fallback: ?action=video&lectureId=11
        String action = req.getParameter("action");
        String lectureIdParam = req.getParameter("lectureId");
        if ("video".equalsIgnoreCase(action) && lectureIdParam != null) {
            handleVideoUrlRequest(req, resp, lectureIdParam);
            return;
        }

        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid API endpoint.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";
        String[] parts = pathInfo.split("/");

        // Route: POST /api/lectures/{id}/progress
        if (parts.length >= 3 && "progress".equalsIgnoreCase(parts[2])) {
            handleProgressSave(req, resp, parts[1]);
            return;
        }

        // Query param fallback: POST /api/lectures?action=progress&lectureId=11
        String action = req.getParameter("action");
        String lectureIdParam = req.getParameter("lectureId");
        if ("progress".equalsIgnoreCase(action) && lectureIdParam != null) {
            handleProgressSave(req, resp, lectureIdParam);
            return;
        }

        sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid API endpoint.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. GET /api/lectures/{id}/video — Critical Security Flow
    // ─────────────────────────────────────────────────────────────────────────
    private void handleVideoUrlRequest(HttpServletRequest req, HttpServletResponse resp, String lectureIdStr)
            throws IOException {

        // 1. JWT / Session Authentication
        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required. Please log in.");
            return;
        }

        int lectureId;
        try {
            lectureId = Integer.parseInt(lectureIdStr);
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid lecture ID.");
            return;
        }

        // 2. Fetch lecture & parent course
        Lecture lecture = lectureDAO.getLectureById(lectureId);
        if (lecture == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Lecture not found.");
            return;
        }

        Course course = courseDAO.getCourseById(lecture.getCourseId());
        if (course == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Parent course not found.");
            return;
        }

        // 3. Status checks: both course & lecture must be PUBLISHED (unless admin)
        String userRole = SessionUtil.getUserRole(req);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(userRole);

        if (!isAdmin) {
            if (!"PUBLISHED".equalsIgnoreCase(course.getStatus())) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Course is not published.");
                return;
            }
            if (!"PUBLISHED".equalsIgnoreCase(lecture.getStatus())) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Lecture is not published.");
                return;
            }

            // 4. Access check: auto-enroll student if accessing lecture video
            if (!lecture.isPreview()) {
                boolean enrolled = enrollmentDAO.isEnrolled(userId, lecture.getCourseId());
                if (!enrolled) {
                    enrollmentDAO.enroll(userId, lecture.getCourseId());
                }
            }
        }

        // 5. Verify video asset exists
        String publicId = lecture.getCloudinaryPublicId();
        if (publicId == null || publicId.trim().isEmpty()) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Video asset not available for this lecture.");
            return;
        }

        // 6. Rate Limiting check (Max 60 video requests per 15 min per IP)
        String clientIp = getClientIp(req);
        int recentCount = videoAccessLogDAO.countRecentAccessesByIp(clientIp, 15);
        if (recentCount > 60) {
            sendError(resp, 429, "Rate limit exceeded. Too many video requests. Please wait.");
            return;
        }

        // 7. Audit Logging
        String userAgent = req.getHeader("User-Agent");
        videoAccessLogDAO.logAccess(userId, lectureId, clientIp, userAgent);

        // 8. Generate 5-minute (300s) authenticated signed Cloudinary URL
        int expiresInSeconds = 300;
        CloudinaryUtil.VideoUrlResult urlResult = CloudinaryUtil.generateProtectedVideoUrl(publicId, expiresInSeconds);

        if (urlResult == null) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate video stream URL.");
            return;
        }

        // 9. Return JSON payload
        JSONObject json = new JSONObject();
        json.put("status", "success");
        json.put("videoUrl", urlResult.getVideoUrl());
        json.put("expiresIn", urlResult.getExpiresIn());
        json.put("expiresAt", urlResult.getExpiresAt());
        json.put("lectureId", lecture.getLectureId());
        json.put("courseId", lecture.getCourseId());
        json.put("title", lecture.getTitle());
        json.put("durationSeconds", lecture.getDurationSeconds());
        json.put("isPreview", lecture.isPreview());

        resp.getWriter().write(json.toString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. POST /api/lectures/{id}/progress — Auto-Save Watch Progress
    // ─────────────────────────────────────────────────────────────────────────
    private void handleProgressSave(HttpServletRequest req, HttpServletResponse resp, String lectureIdStr)
            throws IOException {

        Integer userId = SessionUtil.getUserId(req);
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required.");
            return;
        }

        int lectureId;
        try {
            lectureId = Integer.parseInt(lectureIdStr);
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid lecture ID.");
            return;
        }

        // Read progress data (JSON or Form Params)
        int progressSeconds = 0;
        boolean forceCompleted = false;

        String contentType = req.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            if (sb.length() > 0) {
                try {
                    JSONObject body = new JSONObject(sb.toString());
                    progressSeconds = body.optInt("progressSeconds", 0);
                    forceCompleted = body.optBoolean("completed", false);
                } catch (Exception ignored) {}
            }
        } else {
            String progParam = req.getParameter("progressSeconds");
            if (progParam != null) {
                try { progressSeconds = Integer.parseInt(progParam); } catch (NumberFormatException ignored) {}
            }
            forceCompleted = "true".equalsIgnoreCase(req.getParameter("completed"));
        }

        Lecture lecture = lectureDAO.getLectureById(lectureId);
        if (lecture == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Lecture not found.");
            return;
        }

        // Auto-complete rule: if student reached 90% of duration, mark completed
        boolean completed = forceCompleted;
        if (!completed && lecture.getDurationSeconds() > 0) {
            int threshold = (int) (lecture.getDurationSeconds() * 0.90);
            if (progressSeconds >= threshold) {
                completed = true;
            }
        }

        // Upsert progress with GREATEST logic
        boolean saved = lectureDAO.saveLectureProgress(lectureId, userId, progressSeconds, completed);

        // Recalculate course enrollment progress
        int coursePercentage = enrollmentDAO.recalculateCourseProgress(userId, lecture.getCourseId());

        JSONObject json = new JSONObject();
        json.put("status", saved ? "success" : "failed");
        json.put("lectureId", lectureId);
        json.put("progressSeconds", progressSeconds);
        json.put("completed", completed);
        json.put("courseProgressPercentage", coursePercentage);

        resp.getWriter().write(json.toString());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. GET /api/lectures/{id} — Lecture Metadata
    // ─────────────────────────────────────────────────────────────────────────
    private void handleLectureDetailRequest(HttpServletRequest req, HttpServletResponse resp, String lectureIdStr)
            throws IOException {
        int lectureId;
        try {
            lectureId = Integer.parseInt(lectureIdStr);
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid lecture ID.");
            return;
        }

        Integer userId = SessionUtil.getUserId(req);
        Lecture lecture = (userId != null)
                ? lectureDAO.getLectureWithProgress(lectureId, userId)
                : lectureDAO.getLectureById(lectureId);

        if (lecture == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Lecture not found.");
            return;
        }

        JSONObject json = new JSONObject();
        json.put("id", lecture.getLectureId());
        json.put("courseId", lecture.getCourseId());
        json.put("title", lecture.getTitle());
        json.put("description", lecture.getDescription());
        json.put("lectureNumber", lecture.getLectureNumber());
        json.put("durationSeconds", lecture.getDurationSeconds());
        json.put("isPreview", lecture.isPreview());
        json.put("status", lecture.getStatus());
        json.put("hasVideo", lecture.hasVideo());

        if (userId != null) {
            json.put("completed", lecture.isCompleted());
            json.put("progressSeconds", lecture.getProgressSeconds());
            json.put("watchProgressPercent", lecture.getWatchProgressPercent());
        }

        resp.getWriter().write(json.toString());
    }

    private String getClientIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        JSONObject err = new JSONObject();
        err.put("status", "error");
        err.put("statusCode", statusCode);
        err.put("message", message);
        resp.getWriter().write(err.toString());
    }
}
