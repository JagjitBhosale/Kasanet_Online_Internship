package com.kasnet.dao;

import com.kasnet.model.User;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User entity.
 * Handles all database operations related to user accounts.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public User registerUser(User user) {
        final String sqlWithBranch = "INSERT INTO users "
                + "(first_name, last_name, email, password_hash, mobile, country_code, role, stream, branch, college_name, city, referral_code, is_active, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        final String sqlWithoutBranch = "INSERT INTO users "
                + "(first_name, last_name, email, password_hash, mobile, country_code, role, stream, college_name, city, referral_code, is_active, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            boolean hasBranch = true;
            try {
                ps = conn.prepareStatement(sqlWithBranch, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPasswordHash());
                ps.setString(5, user.getMobile());
                ps.setString(6, user.getCountryCode() != null ? user.getCountryCode() : "+91");
                ps.setString(7, user.getRole() != null ? user.getRole() : "STUDENT");
                ps.setString(8, user.getStream());
                ps.setString(9, user.getBranch());
                ps.setString(10, user.getCollegeName());
                ps.setString(11, user.getCity());
                ps.setString(12, user.getReferralCode());
                ps.setBoolean(13, true);
                ps.executeUpdate();
            } catch (SQLException ex) {
                // If branch column does not exist yet in schema, fallback to legacy insert
                hasBranch = false;
                if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
                ps = conn.prepareStatement(sqlWithoutBranch, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPasswordHash());
                ps.setString(5, user.getMobile());
                ps.setString(6, user.getCountryCode() != null ? user.getCountryCode() : "+91");
                ps.setString(7, user.getRole() != null ? user.getRole() : "STUDENT");
                ps.setString(8, user.getStream());
                ps.setString(9, user.getCollegeName());
                ps.setString(10, user.getCity());
                ps.setString(11, user.getReferralCode());
                ps.setBoolean(12, true);
                ps.executeUpdate();
            }

            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setUserId(generatedKeys.getInt(1));
            }
            return user;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "registerUser: failed for email=" + user.getEmail(), e);
            return null;
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
    }

    public User getUserByEmail(String email) {
        final String sql = "SELECT * FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getUserByEmail: failed for email=" + email, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    public User getUserByEmailForLogin(String email) throws SQLException {
        final String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRowToUser(rs) : null;
            }
        }
    }

    public User getUserById(int userId) {
        final String sql = "SELECT * FROM users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getUserById: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    public boolean emailExists(String email) {
        final String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "emailExists: failed for email=" + email, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return false;
    }

    public boolean mobileExists(String mobile) {
        final String sql = "SELECT COUNT(*) FROM users WHERE mobile = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, mobile);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "mobileExists: failed for mobile=" + mobile, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return false;
    }

    public boolean updateUser(User user) {
        final String sql = "UPDATE users SET first_name = ?, last_name = ?, mobile = ?, city = ?, updated_at = NOW() WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getMobile());
            ps.setString(4, user.getCity());
            ps.setInt(5, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateUser: failed for userId=" + user.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updateLastLogin(int userId) {
        final String updateUser = "UPDATE student_profiles SET previous_login = last_login, last_login = NOW() WHERE user_id = ?";
        final String insertActivity = "INSERT INTO login_activity (user_id, login_time, ip_address) VALUES (?, NOW(), '127.0.0.1')";

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psActivity = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            psUser = conn.prepareStatement(updateUser);
            psUser.setInt(1, userId);
            psUser.executeUpdate();

            psActivity = conn.prepareStatement(insertActivity);
            psActivity.setInt(1, userId);
            psActivity.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateLastLogin: failed for userId=" + userId, e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            closeResources(null, psUser, null);
            closeResources(null, psActivity, null);
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        final String sql = "SELECT * FROM users ORDER BY user_id DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAllUsers: failed", e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return users;
    }

    /**
     * Retrieves all users with role 'STUDENT'.
     */
    public java.util.List<User> getAllStudents() {
        java.util.List<User> students = new java.util.ArrayList<>();
        final String sql = "SELECT * FROM users WHERE role = 'STUDENT' ORDER BY user_id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                students.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAllStudents: failed", e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return students;
    }

    /**
     * Deletes a user by their user ID.
     */
    public boolean deleteUser(int userId) {
        final String sql = "DELETE FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteUser: failed for userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    /**
     * Deletes multiple users by their IDs.
     */
    public boolean deleteUsers(java.util.List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) return false;
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        final String sql = "DELETE FROM users WHERE user_id IN (" + placeholders + ")";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < userIds.size(); i++) {
                ps.setInt(i + 1, userIds.get(i));
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteUsers: failed", e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setMobile(rs.getString("mobile"));
        user.setCountryCode(rs.getString("country_code"));
        user.setRole(rs.getString("role"));
        user.setStream(rs.getString("stream"));
        try {
            user.setBranch(rs.getString("branch"));
        } catch (SQLException ignored) {}
        user.setCollegeName(rs.getString("college_name"));
        user.setCity(rs.getString("city"));
        user.setReferralCode(rs.getString("referral_code"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
        if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        if (conn != null) { try { conn.close(); } catch (SQLException ignored) {} }
    }
}
