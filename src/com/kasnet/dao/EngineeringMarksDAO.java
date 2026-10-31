package com.kasnet.dao;

import com.kasnet.model.EngineeringMarks;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Engineering Year-wise Marks (First Year, Second Year, Third Year, Final Year).
 */
public class EngineeringMarksDAO {

    private static final Logger LOGGER = Logger.getLogger(EngineeringMarksDAO.class.getName());
    private static volatile boolean tableVerified = false;

    public EngineeringMarksDAO() {
        ensureTableExists();
    }

    private void ensureTableExists() {
        if (tableVerified) return;
        synchronized (EngineeringMarksDAO.class) {
            if (tableVerified) return;
            String sql = "CREATE TABLE IF NOT EXISTS `engineering_year_marks` ("
                    + "  `mark_id` INT NOT NULL AUTO_INCREMENT,"
                    + "  `user_id` INT NOT NULL,"
                    + "  `year_level` VARCHAR(50) NOT NULL,"
                    + "  `score_type` ENUM('Percentage','CGPA') NOT NULL DEFAULT 'Percentage',"
                    + "  `score_value` DECIMAL(5,2) NOT NULL,"
                    + "  `passing_year` INT DEFAULT NULL,"
                    + "  `college_name` VARCHAR(200) DEFAULT NULL,"
                    + "  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + "  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "  PRIMARY KEY (`mark_id`),"
                    + "  UNIQUE KEY `uq_user_year_level` (`user_id`, `year_level`),"
                    + "  CONSTRAINT `fk_eym_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                tableVerified = true;
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to auto-create engineering_year_marks table: " + e.getMessage());
            }
        }
    }

    public List<EngineeringMarks> getMarksByUserId(int userId) {
        List<EngineeringMarks> list = new ArrayList<>();
        String sql = "SELECT * FROM engineering_year_marks WHERE user_id = ? "
                + "ORDER BY FIELD(year_level, 'First Year', 'Second Year', 'Third Year', 'Final Year'), mark_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getMarksByUserId failed for userId=" + userId, e);
        }
        return list;
    }

    public EngineeringMarks getMarkById(int markId, int userId) {
        String sql = "SELECT * FROM engineering_year_marks WHERE mark_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, markId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getMarkById failed for markId=" + markId, e);
        }
        return null;
    }

    public boolean saveOrUpdateMark(EngineeringMarks mark) {
        String sql = "INSERT INTO engineering_year_marks (user_id, year_level, score_type, score_value, passing_year, college_name, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW()) "
                + "ON DUPLICATE KEY UPDATE score_type = VALUES(score_type), score_value = VALUES(score_value), "
                + "passing_year = VALUES(passing_year), college_name = VALUES(college_name), updated_at = NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mark.getUserId());
            ps.setString(2, mark.getYearLevel());
            ps.setString(3, mark.getScoreType() != null ? mark.getScoreType() : "Percentage");
            ps.setDouble(4, mark.getScoreValue());
            if (mark.getPassingYear() > 0) {
                ps.setInt(5, mark.getPassingYear());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, mark.getCollegeName());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "saveOrUpdateMark failed for user=" + mark.getUserId() + ", year=" + mark.getYearLevel(), e);
        }
        return false;
    }

    public boolean updateMarkById(EngineeringMarks mark) {
        String sql = "UPDATE engineering_year_marks SET year_level = ?, score_type = ?, score_value = ?, "
                + "passing_year = ?, college_name = ?, updated_at = NOW() WHERE mark_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mark.getYearLevel());
            ps.setString(2, mark.getScoreType() != null ? mark.getScoreType() : "Percentage");
            ps.setDouble(3, mark.getScoreValue());
            if (mark.getPassingYear() > 0) {
                ps.setInt(4, mark.getPassingYear());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, mark.getCollegeName());
            ps.setInt(6, mark.getMarkId());
            ps.setInt(7, mark.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateMarkById failed for markId=" + mark.getMarkId(), e);
        }
        return false;
    }

    public boolean deleteMark(int markId, int userId) {
        String sql = "DELETE FROM engineering_year_marks WHERE mark_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, markId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteMark failed for markId=" + markId + ", userId=" + userId, e);
        }
        return false;
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
}
