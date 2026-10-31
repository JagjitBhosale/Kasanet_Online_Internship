package com.kasnet.dao;

import com.kasnet.model.StudentProfile;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the student_profiles table.
 * Also handles profile-completion percentage calculation across tables.
 */
public class ProfileDAO {

    private static final Logger LOGGER = Logger.getLogger(ProfileDAO.class.getName());

    public StudentProfile getProfileByUserId(int userId) {
        final String sql =
                "SELECT profile_id, user_id, gender, date_of_birth, address, state, "
                + "profile_photo_path, resume_path, resume_filename, resume_uploaded_at, "
                + "profile_completion, last_login, previous_login, created_at, updated_at "
                + "FROM student_profiles WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToProfile(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getProfileByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    public boolean createProfile(int userId) {
        final String sql =
                "INSERT INTO student_profiles (user_id, profile_completion, created_at, updated_at) "
                + "VALUES (?, 20, NOW(), NOW())";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "createProfile: failed for userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updatePersonalDetails(StudentProfile profile) {
        final String sql =
                "UPDATE student_profiles SET "
                + "gender = ?, date_of_birth = ?, address = ?, state = ?, updated_at = NOW() "
                + "WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, profile.getGender());
            ps.setDate(2, profile.getDateOfBirth());
            ps.setString(3, profile.getAddress());
            ps.setString(4, profile.getState());
            ps.setInt(5, profile.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updatePersonalDetails: failed for userId=" + profile.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    /**
     * Calculates dynamic profile completion (0-100%).
     * Weights:
     * - Basic User Account: 20%
     * - Personal Details (Gender, DOB, Address): 20%
     * - Education Records: 25%
     * - Skills (Technical/Soft): 15%
     * - Projects: 10%
     * - Certifications or Additional Activities: 10%
     */
    public int calculateProfileCompletion(int userId) {
        int score = 0;
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();

            // Basic registration account exists: 20%
            score += 20;

            // Personal details (20%)
            final String personalSql = "SELECT gender, date_of_birth, address FROM student_profiles WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(personalSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getString("gender") != null || rs.getDate("date_of_birth") != null || rs.getString("address") != null) {
                            score += 20;
                        }
                    }
                }
            }

            // Education details (25%)
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM education_details WHERE user_id = ?", 25);

            // Skills (15%)
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM skills WHERE user_id = ?", 15);

            // Projects (10%)
            score += sectionScore(conn, userId, "SELECT COUNT(*) FROM projects WHERE user_id = ?", 10);

            // Certifications or Additional Activities (10%)
            int actScore = sectionScore(conn, userId, "SELECT COUNT(*) FROM certifications WHERE user_id = ?", 10);
            if (actScore == 0) {
                actScore = sectionScore(conn, userId, "SELECT COUNT(*) FROM hackathons WHERE user_id = ?", 10);
            }
            if (actScore == 0) {
                actScore = sectionScore(conn, userId, "SELECT COUNT(*) FROM intercollege_competitions WHERE user_id = ?", 10);
            }
            score += actScore;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "calculateProfileCompletion: failed for userId=" + userId, e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }

        return Math.min(score, 100);
    }

    private int sectionScore(Connection conn, int userId, String countSql, int weight) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return weight;
                }
            }
        }
        return 0;
    }

    public boolean updateProfileCompletion(int userId, int completion) {
        final String sql = "UPDATE student_profiles SET profile_completion = ?, updated_at = NOW() WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, Math.min(Math.max(completion, 0), 100));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateProfileCompletion: failed for userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    private StudentProfile mapRowToProfile(ResultSet rs) throws SQLException {
        StudentProfile p = new StudentProfile();
        p.setProfileId(rs.getInt("profile_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setGender(rs.getString("gender"));
        p.setDateOfBirth(rs.getDate("date_of_birth"));
        p.setAddress(rs.getString("address"));
        p.setState(rs.getString("state"));
        p.setProfilePhotoPath(rs.getString("profile_photo_path"));
        p.setResumePath(rs.getString("resume_path"));
        p.setResumeFilename(rs.getString("resume_filename"));
        p.setResumeUploadedAt(rs.getTimestamp("resume_uploaded_at"));
        p.setProfileCompletion(rs.getInt("profile_completion"));
        p.setLastLogin(rs.getTimestamp("last_login"));
        p.setPreviousLogin(rs.getTimestamp("previous_login"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
        if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        if (conn != null) { try { conn.close(); } catch (SQLException ignored) {} }
    }
}
