package com.kasnet.dao;

import com.kasnet.model.Enrollment;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Enrollment entities.
 * Handles student course enrollment and progress synchronization.
 */
public class EnrollmentDAO {

    // -------------------------------------------------------------------------
    // Enroll a student in a course
    // -------------------------------------------------------------------------
    public boolean enroll(int userId, int courseId) {
        String sql = "INSERT IGNORE INTO enrollments (user_id, course_id, progress_percentage, completed_lessons) " +
                     "VALUES (?, ?, 0, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Check enrollment status
    // -------------------------------------------------------------------------
    public boolean isEnrolled(int userId, int courseId) {
        String sql = "SELECT 1 FROM enrollments WHERE user_id = ? AND course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Get enrollment for a specific user + course
    // -------------------------------------------------------------------------
    public Enrollment getEnrollment(int userId, int courseId) {
        String sql = "SELECT * FROM enrollments WHERE user_id = ? AND course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, courseId);
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
    // List all enrollments for a user
    // -------------------------------------------------------------------------
    public List<Enrollment> getEnrollmentsByUser(int userId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.title AS course_title " +
                     "FROM enrollments e " +
                     "JOIN courses c ON c.id = e.course_id " +
                     "WHERE e.user_id = ? " +
                     "ORDER BY e.enrolled_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment en = mapRow(rs);
                    en.setCourseTitle(rs.getString("course_title"));
                    list.add(en);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // Update progress directly
    // -------------------------------------------------------------------------
    public boolean updateProgress(int userId, int courseId, int progressPercentage, int completedLessons) {
        String sql = "UPDATE enrollments SET progress_percentage = ?, completed_lessons = ?, " +
                     "completed_at = CASE WHEN ? >= 100 THEN CURRENT_TIMESTAMP ELSE completed_at END " +
                     "WHERE user_id = ? AND course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progressPercentage);
            ps.setInt(2, completedLessons);
            ps.setInt(3, progressPercentage);
            ps.setInt(4, userId);
            ps.setInt(5, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Recalculate and update enrollment progress from lecture_progress table
    // -------------------------------------------------------------------------
    public int recalculateCourseProgress(int userId, int courseId) {
        String countSql =
            "SELECT " +
            "  (SELECT COUNT(*) FROM lectures WHERE course_id = ? AND status = 'PUBLISHED') AS total_lectures, " +
            "  (SELECT COUNT(*) FROM lecture_progress lp " +
            "   JOIN lectures l ON l.id = lp.lecture_id " +
            "   WHERE lp.user_id = ? AND l.course_id = ? AND lp.completed = 1 AND l.status = 'PUBLISHED') AS completed_lectures";

        int totalLectures = 0;
        int completedLectures = 0;
        int percentage = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, userId);
            ps.setInt(3, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalLectures = rs.getInt("total_lectures");
                    completedLectures = rs.getInt("completed_lectures");
                }
            }

            if (totalLectures > 0) {
                percentage = Math.min(100, (completedLectures * 100) / totalLectures);
            }

            updateProgress(userId, courseId, percentage, completedLectures);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return percentage;
    }

    // -------------------------------------------------------------------------
    // Count enrolled courses for a user
    // -------------------------------------------------------------------------
    public int countEnrollments(int userId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // Row mapper
    // -------------------------------------------------------------------------
    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("id"));
        e.setUserId(rs.getInt("user_id"));
        e.setCourseId(rs.getInt("course_id"));
        e.setProgressPercentage(rs.getInt("progress_percentage"));
        e.setCompletedLessons(rs.getInt("completed_lessons"));
        e.setEnrolledAt(rs.getTimestamp("enrolled_at"));
        e.setCompletedAt(rs.getTimestamp("completed_at"));
        return e;
    }
}
