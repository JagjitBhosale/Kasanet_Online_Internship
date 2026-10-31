package com.kasnet.dao;

import com.kasnet.model.Course;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course entities.
 * Provides CRUD and query methods for the courses table.
 */
public class CourseDAO {

    // -------------------------------------------------------------------------
    // Row mapper helper
    // -------------------------------------------------------------------------
    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setSlug(rs.getString("slug"));
        c.setDescription(rs.getString("description"));
        c.setThumbnailUrl(rs.getString("thumbnail_url"));
        c.setInstructorName(rs.getString("instructor_name"));
        c.setCategory(rs.getString("category"));
        c.setDifficulty(rs.getString("difficulty"));
        c.setDurationWeeks(rs.getInt("duration_weeks"));
        c.setTotalLessons(rs.getInt("total_lessons"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }

    // -------------------------------------------------------------------------
    // List all PUBLISHED courses with lecture & enrollment counts (Student view)
    // -------------------------------------------------------------------------
    public List<Course> getPublishedCourses(int userId) {
        List<Course> list = new ArrayList<>();
        String sql =
            "SELECT c.*, " +
            "  (SELECT COUNT(*) FROM lectures l WHERE l.course_id = c.id AND l.status = 'PUBLISHED') AS lecture_count, " +
            "  (SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.id) AS enrollment_count, " +
            "  IFNULL(en.progress_percentage, 0)  AS user_progress, " +
            "  IFNULL(en.completed_lessons, 0)    AS user_completed_lessons, " +
            "  CASE WHEN en.id IS NOT NULL THEN 1 ELSE 0 END AS is_enrolled " +
            "FROM courses c " +
            "LEFT JOIN enrollments en ON en.course_id = c.id AND en.user_id = ? " +
            "WHERE c.status = 'PUBLISHED' " +
            "ORDER BY c.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = mapRow(rs);
                    c.setLectureCount(rs.getInt("lecture_count"));
                    c.setEnrollmentCount(rs.getInt("enrollment_count"));
                    c.setEnrolled(rs.getBoolean("is_enrolled"));
                    c.setUserProgress(rs.getInt("user_progress"));
                    c.setUserCompletedLessons(rs.getInt("user_completed_lessons"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // List ALL courses (Admin view - includes DRAFT, PUBLISHED, ARCHIVED)
    // -------------------------------------------------------------------------
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql =
            "SELECT c.*, " +
            "  (SELECT COUNT(*) FROM lectures l WHERE l.course_id = c.id) AS lecture_count, " +
            "  (SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.id) AS enrollment_count " +
            "FROM courses c " +
            "ORDER BY c.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = mapRow(rs);
                c.setLectureCount(rs.getInt("lecture_count"));
                c.setEnrollmentCount(rs.getInt("enrollment_count"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // Find by ID
    // -------------------------------------------------------------------------
    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
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
    // Find by slug
    // -------------------------------------------------------------------------
    public Course getCourseBySlug(String slug) {
        String sql = "SELECT * FROM courses WHERE slug = ? AND status = 'PUBLISHED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slug);
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
    // Create new course (Admin)
    // -------------------------------------------------------------------------
    public int createCourse(Course course) {
        String sql = "INSERT INTO courses (title, slug, description, thumbnail_url, instructor_name, " +
                     "category, difficulty, duration_weeks, total_lessons, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getSlug() != null && !course.getSlug().isEmpty()
                    ? course.getSlug() : course.generateSlug());
            ps.setString(3, course.getDescription());
            ps.setString(4, course.getThumbnailUrl());
            ps.setString(5, course.getInstructorName());
            ps.setString(6, course.getCategory() != null ? course.getCategory() : "Engineering");
            ps.setString(7, course.getDifficulty() != null ? course.getDifficulty() : "Beginner");
            ps.setInt(8, course.getDurationWeeks() > 0 ? course.getDurationWeeks() : 6);
            ps.setInt(9, course.getTotalLessons());
            ps.setString(10, course.getStatus() != null ? course.getStatus() : "PUBLISHED");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        course.setCourseId(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // Update course (Admin)
    // -------------------------------------------------------------------------
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET title = ?, slug = ?, description = ?, thumbnail_url = ?, " +
                     "instructor_name = ?, category = ?, difficulty = ?, duration_weeks = ?, " +
                     "total_lessons = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getSlug() != null && !course.getSlug().isEmpty()
                    ? course.getSlug() : course.generateSlug());
            ps.setString(3, course.getDescription());
            ps.setString(4, course.getThumbnailUrl());
            ps.setString(5, course.getInstructorName());
            ps.setString(6, course.getCategory());
            ps.setString(7, course.getDifficulty());
            ps.setInt(8, course.getDurationWeeks());
            ps.setInt(9, course.getTotalLessons());
            ps.setString(10, course.getStatus());
            ps.setInt(11, course.getCourseId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Delete course (Admin) - cascades to lectures & enrollments
    // -------------------------------------------------------------------------
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Count published courses
    // -------------------------------------------------------------------------
    public int countPublishedCourses() {
        String sql = "SELECT COUNT(*) FROM courses WHERE status = 'PUBLISHED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // Count enrollments for a specific course
    // -------------------------------------------------------------------------
    public int countEnrollmentsByCourse(int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
