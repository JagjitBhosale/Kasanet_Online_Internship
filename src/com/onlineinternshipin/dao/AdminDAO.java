package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.User;
import com.onlineinternshipin.utility.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the production 'admin' table
 * defined in Database/onlineinternship.sql.
 *
 * Columns:
 * - id       (INT, Primary Key)
 * - fname    (VARCHAR(100))
 * - lname    (VARCHAR(150))
 * - email    (VARCHAR(150))
 * - password (VARCHAR(20))
 */
public class AdminDAO {

    private static final Logger LOGGER = Logger.getLogger(AdminDAO.class.getName());

    /**
     * Retrieves an admin record by email address.
     *
     * @param email the admin email address
     * @return User object representing admin, or null if not found
     */
    public User getAdminByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        final String sql = "SELECT id, fname, lname, email, password FROM `admin` WHERE LOWER(`email`) = LOWER(?)";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email.trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToAdmin(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAdminByEmail failed for email=" + email, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    /**
     * Retrieves an admin record by admin ID.
     *
     * @param id the admin primary key
     * @return User object representing admin, or null if not found
     */
    public User getAdminById(int id) {
        if (id <= 0) return null;
        final String sql = "SELECT id, fname, lname, email, password FROM `admin` WHERE `id` = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToAdmin(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAdminById failed for id=" + id, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    /**
     * Retrieves all admin accounts.
     *
     * @return List of admin User objects
     */
    public List<User> getAllAdmins() {
        List<User> list = new ArrayList<>();
        final String sql = "SELECT id, fname, lname, email, password FROM `admin` ORDER BY `id` ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToAdmin(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getAllAdmins failed", e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    private User mapRowToAdmin(ResultSet rs) throws SQLException {
        User admin = new User();
        admin.setUserId(rs.getInt("id"));
        admin.setFirstName(rs.getString("fname"));
        admin.setLastName(rs.getString("lname"));
        admin.setEmail(rs.getString("email"));
        admin.setPasswordHash(rs.getString("password"));
        admin.setRole("ADMIN");
        admin.setActive(true);
        return admin;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
        if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        if (conn != null) { try { conn.close(); } catch (SQLException ignored) {} }
    }
}
