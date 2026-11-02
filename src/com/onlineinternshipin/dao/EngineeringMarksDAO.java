package com.onlineinternshipin.dao;

import com.onlineinternshipin.model.EngineeringMarks;
import com.onlineinternshipin.utility.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for 'engineering_year_marks' table.
 * Handles adding, editing, deleting, and listing engineering year-wise scores.
 */
public class EngineeringMarksDAO {

    private static final Logger LOGGER = Logger.getLogger(EngineeringMarksDAO.class.getName());

    public List<EngineeringMarks> getMarksByUserId(int userId) {
        List<EngineeringMarks> list = new ArrayList<>();
        if (userId <= 0) return list;

        final String sql = "SELECT mark_id, user_id, year_level, score_type, score_value, passing_year, college_name, created_at, updated_at "
                + "FROM `engineering_year_marks` WHERE user_id = ? ORDER BY mark_id ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getMarksByUserId failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    public EngineeringMarks getMarksById(int markId, int userId) {
        if (markId <= 0 || userId <= 0) return null;

        final String sql = "SELECT mark_id, user_id, year_level, score_type, score_value, passing_year, college_name, created_at, updated_at "
                + "FROM `engineering_year_marks` WHERE mark_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, markId);
            ps.setInt(2, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getMarksById failed for markId=" + markId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    public boolean saveOrUpdateMark(EngineeringMarks m) {
        if (m == null || m.getUserId() <= 0 || m.getYearLevel() == null) return false;

        final String sql = "INSERT INTO `engineering_year_marks` "
                + "(user_id, year_level, score_type, score_value, passing_year, college_name, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW()) "
                + "ON DUPLICATE KEY UPDATE "
                + "score_type = VALUES(score_type), "
                + "score_value = VALUES(score_value), "
                + "passing_year = VALUES(passing_year), "
                + "college_name = VALUES(college_name), "
                + "updated_at = NOW()";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getUserId());
            ps.setString(2, m.getYearLevel());
            ps.setString(3, m.getScoreType() != null ? m.getScoreType() : "Percentage");
            ps.setDouble(4, m.getScoreValue());
            if (m.getPassingYear() > 0) {
                ps.setInt(5, m.getPassingYear());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, m.getCollegeName());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "saveOrUpdateMark failed for userId=" + m.getUserId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean updateMarkById(EngineeringMarks m) {
        if (m == null || m.getMarkId() <= 0 || m.getUserId() <= 0) return false;

        final String sql = "UPDATE `engineering_year_marks` SET "
                + "year_level = ?, score_type = ?, score_value = ?, passing_year = ?, college_name = ?, updated_at = NOW() "
                + "WHERE mark_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, m.getYearLevel());
            ps.setString(2, m.getScoreType() != null ? m.getScoreType() : "Percentage");
            ps.setDouble(3, m.getScoreValue());
            if (m.getPassingYear() > 0) {
                ps.setInt(4, m.getPassingYear());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, m.getCollegeName());
            ps.setInt(6, m.getMarkId());
            ps.setInt(7, m.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateMarkById failed for markId=" + m.getMarkId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean deleteMark(int markId, Integer userId) {
        if (markId <= 0 || userId == null || userId <= 0) return false;

        final String sql = "DELETE FROM `engineering_year_marks` WHERE mark_id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, markId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteMark failed for markId=" + markId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public boolean deleteMarks(int markId, int userId) {
        return deleteMark(markId, userId);
    }

    public boolean saveOrUpdateMarks(EngineeringMarks m) {
        return saveOrUpdateMark(m);
    }

    private EngineeringMarks mapRow(ResultSet rs) throws SQLException {
        EngineeringMarks m = new EngineeringMarks();
        m.setMarkId(rs.getInt("mark_id"));
        m.setUserId(rs.getInt("user_id"));
        m.setYearLevel(rs.getString("year_level"));
        m.setScoreType(rs.getString("score_type"));
        m.setScoreValue(rs.getDouble("score_value"));
        m.setPassingYear(rs.getInt("passing_year"));
        m.setCollegeName(rs.getString("college_name"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setUpdatedAt(rs.getTimestamp("updated_at"));
        return m;
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
        if (ps != null) { try { ps.close(); } catch (SQLException ignored) {} }
        if (conn != null) { try { conn.close(); } catch (SQLException ignored) {} }
    }
}
