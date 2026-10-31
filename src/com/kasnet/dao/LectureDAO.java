package com.kasnet.dao;

import com.kasnet.model.Lecture;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Lecture entities and Lecture Progress tracking.
 */
public class LectureDAO {

    // -------------------------------------------------------------------------
    // Row mapper helper
    // -------------------------------------------------------------------------
    private Lecture mapRow(ResultSet rs) throws SQLException {
        Lecture l = new Lecture();
        l.setLectureId(rs.getInt("id"));
        l.setCourseId(rs.getInt("course_id"));
        l.setTitle(rs.getString("title"));
        l.setDescription(rs.getString("description"));
        l.setLectureNumber(rs.getInt("lecture_number"));
        l.setCloudinaryPublicId(rs.getString("cloudinary_public_id"));
        l.setDurationSeconds(rs.getInt("duration_seconds"));
        l.setPreview(rs.getBoolean("is_preview"));
        l.setStatus(rs.getString("status"));
        l.setCreatedAt(rs.getTimestamp("created_at"));
        return l;
    }

    // -------------------------------------------------------------------------
    // Student: Get lectures for a specific course with user progress
    // -------------------------------------------------------------------------
    public List<Lecture> getLecturesByCourseWithProgress(int courseId, int userId) {
        List<Lecture> list = new ArrayList<>();
        String sql =
            "SELECT l.*, " +
            "  IFNULL(p.completed, 0)        AS is_completed, " +
            "  IFNULL(p.progress_seconds, 0) AS progress_seconds, " +
            "  p.last_watched_at " +
            "FROM lectures l " +
            "LEFT JOIN lecture_progress p ON l.id = p.lecture_id AND p.user_id = ? " +
            "WHERE l.course_id = ? AND l.status = 'PUBLISHED' " +
            "ORDER BY l.lecture_number ASC, l.id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lecture l = mapRow(rs);
                    l.setCompleted(rs.getBoolean("is_completed"));
                    l.setProgressSeconds(rs.getInt("progress_seconds"));
                    l.setLastWatched(rs.getTimestamp("last_watched_at"));

                    // Calculate watch progress percent
                    if (l.getDurationSeconds() > 0) {
                        int percent = (int) Math.min(100, Math.round(((double) l.getProgressSeconds() / l.getDurationSeconds()) * 100));
                        l.setWatchProgressPercent(percent);
                    } else if (l.isCompleted()) {
                        l.setWatchProgressPercent(100);
                    }
                    list.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // Student: Get all published lectures across all courses with user progress
    // -------------------------------------------------------------------------
    public List<Lecture> getLecturesWithProgress(int userId) {
        List<Lecture> list = new ArrayList<>();
        String sql =
            "SELECT l.*, " +
            "  IFNULL(p.completed, 0)        AS is_completed, " +
            "  IFNULL(p.progress_seconds, 0) AS progress_seconds, " +
            "  p.last_watched_at " +
            "FROM lectures l " +
            "LEFT JOIN lecture_progress p ON l.id = p.lecture_id AND p.user_id = ? " +
            "WHERE l.status = 'PUBLISHED' " +
            "ORDER BY l.course_id ASC, l.lecture_number ASC, l.id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lecture l = mapRow(rs);
                    l.setCompleted(rs.getBoolean("is_completed"));
                    l.setProgressSeconds(rs.getInt("progress_seconds"));
                    l.setLastWatched(rs.getTimestamp("last_watched_at"));
                    if (l.getDurationSeconds() > 0) {
                        int percent = (int) Math.min(100, Math.round(((double) l.getProgressSeconds() / l.getDurationSeconds()) * 100));
                        l.setWatchProgressPercent(percent);
                    } else if (l.isCompleted()) {
                        l.setWatchProgressPercent(100);
                    }
                    list.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // Admin: Get all lectures for a course (including DRAFT)
    // -------------------------------------------------------------------------
    public List<Lecture> getAllLecturesByCourse(int courseId) {
        List<Lecture> list = new ArrayList<>();
        String sql = "SELECT * FROM lectures WHERE course_id = ? ORDER BY lecture_number ASC, id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // Find lecture by ID
    // -------------------------------------------------------------------------
    public Lecture getLectureById(int lectureId) {
        String sql = "SELECT * FROM lectures WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lectureId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Find lecture by ID with progress for a specific user
    // -------------------------------------------------------------------------
    public Lecture getLectureWithProgress(int lectureId, int userId) {
        String sql =
            "SELECT l.*, " +
            "  IFNULL(p.completed, 0)        AS is_completed, " +
            "  IFNULL(p.progress_seconds, 0) AS progress_seconds, " +
            "  p.last_watched_at " +
            "FROM lectures l " +
            "LEFT JOIN lecture_progress p ON l.id = p.lecture_id AND p.user_id = ? " +
            "WHERE l.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, lectureId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Lecture l = mapRow(rs);
                    l.setCompleted(rs.getBoolean("is_completed"));
                    l.setProgressSeconds(rs.getInt("progress_seconds"));
                    l.setLastWatched(rs.getTimestamp("last_watched_at"));
                    if (l.getDurationSeconds() > 0) {
                        int percent = (int) Math.min(100, Math.round(((double) l.getProgressSeconds() / l.getDurationSeconds()) * 100));
                        l.setWatchProgressPercent(percent);
                    } else if (l.isCompleted()) {
                        l.setWatchProgressPercent(100);
                    }
                    return l;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Upsert watch progress using GREATEST logic (never rollback, never uncomplete)
    // -------------------------------------------------------------------------
    public boolean saveLectureProgress(int lectureId, int userId, int progressSeconds, boolean completed) {
        String sql =
            "INSERT INTO lecture_progress (user_id, lecture_id, progress_seconds, completed, last_watched_at) " +
            "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
            "ON DUPLICATE KEY UPDATE " +
            "  progress_seconds = GREATEST(progress_seconds, VALUES(progress_seconds)), " +
            "  completed = CASE WHEN completed = 1 THEN 1 ELSE VALUES(completed) END, " +
            "  last_watched_at = CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, lectureId);
            ps.setInt(3, progressSeconds);
            ps.setBoolean(4, completed);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Legacy support for mark completed
    // -------------------------------------------------------------------------
    public boolean markLectureCompleted(int lectureId, int userId) {
        Lecture l = getLectureById(lectureId);
        int duration = (l != null && l.getDurationSeconds() > 0) ? l.getDurationSeconds() : 600;
        return saveLectureProgress(lectureId, userId, duration, true);
    }

    // -------------------------------------------------------------------------
    // Legacy support for updateWatchProgress
    // -------------------------------------------------------------------------
    public boolean updateWatchProgress(int lectureId, int userId, int progressPercent) {
        Lecture l = getLectureById(lectureId);
        int duration = (l != null && l.getDurationSeconds() > 0) ? l.getDurationSeconds() : 600;
        int seconds = (int) Math.round((progressPercent / 100.0) * duration);
        boolean completed = progressPercent >= 90;
        return saveLectureProgress(lectureId, userId, seconds, completed);
    }

    // -------------------------------------------------------------------------
    // Admin: Create new lecture
    // -------------------------------------------------------------------------
    public int createLecture(Lecture l) {
        String sql =
            "INSERT INTO lectures (course_id, title, description, lecture_number, cloudinary_public_id, " +
            "duration_seconds, is_preview, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getCourseId());
            ps.setString(2, l.getTitle());
            ps.setString(3, l.getDescription());
            ps.setInt(4, l.getLectureNumber() > 0 ? l.getLectureNumber() : 1);
            ps.setString(5, l.getCloudinaryPublicId());
            ps.setInt(6, l.getDurationSeconds());
            ps.setBoolean(7, l.isPreview());
            ps.setString(8, l.getStatus() != null ? l.getStatus() : "PUBLISHED");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        l.setLectureId(id);
                        // Update total_lessons count in course
                        syncCourseLessonCount(l.getCourseId());
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // Admin: Update lecture details
    // -------------------------------------------------------------------------
    public boolean updateLecture(Lecture l) {
        String sql =
            "UPDATE lectures SET title = ?, description = ?, lecture_number = ?, " +
            "cloudinary_public_id = ?, duration_seconds = ?, is_preview = ?, status = ? " +
            "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getTitle());
            ps.setString(2, l.getDescription());
            ps.setInt(3, l.getLectureNumber());
            ps.setString(4, l.getCloudinaryPublicId());
            ps.setInt(5, l.getDurationSeconds());
            ps.setBoolean(6, l.isPreview());
            ps.setString(7, l.getStatus());
            ps.setInt(8, l.getLectureId());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                syncCourseLessonCount(l.getCourseId());
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Admin: Update lecture video asset
    // -------------------------------------------------------------------------
    public boolean updateLectureVideo(int lectureId, String cloudinaryPublicId, int durationSeconds) {
        String sql = "UPDATE lectures SET cloudinary_public_id = ?, duration_seconds = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cloudinaryPublicId);
            ps.setInt(2, durationSeconds);
            ps.setInt(3, lectureId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Admin: Delete lecture
    // -------------------------------------------------------------------------
    public boolean deleteLecture(int lectureId) {
        Lecture l = getLectureById(lectureId);
        int courseId = (l != null) ? l.getCourseId() : 0;

        String sql = "DELETE FROM lectures WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lectureId);
            boolean ok = ps.executeUpdate() > 0;
            if (ok && courseId > 0) {
                syncCourseLessonCount(courseId);
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Helper: keep course total_lessons in sync
    // -------------------------------------------------------------------------
    private void syncCourseLessonCount(int courseId) {
        String sql = "UPDATE courses SET total_lessons = " +
                     "(SELECT COUNT(*) FROM lectures WHERE course_id = ? AND status = 'PUBLISHED') " +
                     "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Count lectures
    // -------------------------------------------------------------------------
    public int countLecturesByCourse(int courseId) {
        String sql = "SELECT COUNT(*) FROM lectures WHERE course_id = ? AND status = 'PUBLISHED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
