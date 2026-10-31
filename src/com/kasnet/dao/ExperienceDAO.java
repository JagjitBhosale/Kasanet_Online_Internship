package com.kasnet.dao;

import com.kasnet.model.Experience;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the experience table.
 * Enforces ownership on every query and mutation.
 */
public class ExperienceDAO {

    private static final Logger LOGGER = Logger.getLogger(ExperienceDAO.class.getName());

    public List<Experience> getExperienceByUserId(int userId) {
        List<Experience> list = new ArrayList<>();
        final String sql =
                "SELECT experience_id, user_id, company_name, role, employment_type, "
                + "technologies_used, description, start_date, end_date, is_current, created_at, updated_at "
                + "FROM experience WHERE user_id = ? ORDER BY start_date DESC, experience_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getExperienceByUserId: failed for userId=" + userId, e);
        }
        return list;
    }

    public Experience getExperienceById(int experienceId, int userId) {
        final String sql =
                "SELECT experience_id, user_id, company_name, role, employment_type, "
                + "technologies_used, description, start_date, end_date, is_current, created_at, updated_at "
                + "FROM experience WHERE experience_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, experienceId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getExperienceById: failed for expId=" + experienceId + ", userId=" + userId, e);
        }
        return null;
    }

    public boolean addExperience(Experience exp) {
        if (exp == null || exp.getUserId() <= 0) return false;

        final String sql =
                "INSERT INTO experience (user_id, company_name, role, employment_type, "
                + "technologies_used, description, start_date, end_date, is_current, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(    1, exp.getUserId());
            ps.setString( 2, exp.getCompanyName());
            ps.setString( 3, exp.getRole());
            ps.setString( 4, exp.getEmploymentType());
            ps.setString( 5, exp.getTechnologiesUsed());
            ps.setString( 6, exp.getDescription());
            ps.setDate(   7, exp.getStartDate());
            ps.setDate(   8, exp.isCurrent() ? null : exp.getEndDate());
            ps.setBoolean(9, exp.isCurrent());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        exp.setExperienceId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addExperience: failed for userId=" + exp.getUserId(), e);
        }
        return false;
    }

    public boolean updateExperience(Experience exp) {
        if (exp == null || exp.getExperienceId() <= 0 || exp.getUserId() <= 0) return false;

        final String sql =
                "UPDATE experience SET "
                + "company_name = ?, role = ?, employment_type = ?, technologies_used = ?, "
                + "description = ?, start_date = ?, end_date = ?, is_current = ?, updated_at = NOW() "
                + "WHERE experience_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString( 1, exp.getCompanyName());
            ps.setString( 2, exp.getRole());
            ps.setString( 3, exp.getEmploymentType());
            ps.setString( 4, exp.getTechnologiesUsed());
            ps.setString( 5, exp.getDescription());
            ps.setDate(   6, exp.getStartDate());
            ps.setDate(   7, exp.isCurrent() ? null : exp.getEndDate());
            ps.setBoolean(8, exp.isCurrent());
            ps.setInt(    9, exp.getExperienceId());
            ps.setInt(   10, exp.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateExperience: failed for expId=" + exp.getExperienceId() + ", userId=" + exp.getUserId(), e);
        }
        return false;
    }

    public boolean deleteExperience(int experienceId, int userId) {
        final String sql = "DELETE FROM experience WHERE experience_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, experienceId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteExperience: failed for expId=" + experienceId + ", userId=" + userId, e);
        }
        return false;
    }

    private Experience mapRow(ResultSet rs) throws SQLException {
        Experience e = new Experience();
        e.setExperienceId(rs.getInt("experience_id"));
        e.setUserId(rs.getInt("user_id"));
        e.setCompanyName(rs.getString("company_name"));
        e.setRole(rs.getString("role"));
        e.setEmploymentType(rs.getString("employment_type"));
        e.setTechnologiesUsed(rs.getString("technologies_used"));
        e.setDescription(rs.getString("description"));
        e.setStartDate(rs.getDate("start_date"));
        e.setEndDate(rs.getDate("end_date"));
        e.setCurrent(rs.getBoolean("is_current"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        e.setUpdatedAt(rs.getTimestamp("updated_at"));
        return e;
    }
}
