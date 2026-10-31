package com.kasnet.dao;

import com.kasnet.model.AIAnalysisResult;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ProfileAnalysisDAO - Persistence handler for AI profile analysis results
 * and system settings (such as the Groq API key configuration).
 */
public class ProfileAnalysisDAO {

    private static final Logger LOGGER = Logger.getLogger(ProfileAnalysisDAO.class.getName());
    private static volatile boolean schemaReady = false;

    public ProfileAnalysisDAO() {
        ensureSchema();
    }

    public static synchronized void ensureSchema() {
        if (schemaReady) return;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. profile_analysis_history table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `profile_analysis_history` ("
                + "  `analysis_id`   INT          NOT NULL AUTO_INCREMENT,"
                + "  `user_id`       INT          NOT NULL,"
                + "  `target_role`   VARCHAR(150) NOT NULL,"
                + "  `analysis_json` MEDIUMTEXT   NOT NULL,"
                + "  `model_used`    VARCHAR(100) NOT NULL DEFAULT 'llama-3.3-70b-versatile',"
                + "  `status`        VARCHAR(50)  NOT NULL DEFAULT 'COMPLETED',"
                + "  `created_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (`analysis_id`),"
                + "  INDEX `idx_ai_user` (`user_id`),"
                + "  CONSTRAINT `fk_ai_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) "
                + "    ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );

            // 2. system_settings table (for deployment / runtime configs like groq_api_key)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `system_settings` ("
                + "  `setting_key`   VARCHAR(100) NOT NULL,"
                + "  `setting_value` TEXT         DEFAULT NULL,"
                + "  `updated_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (`setting_key`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );

            schemaReady = true;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "ProfileAnalysisDAO: ensureSchema encountered: " + e.getMessage());
        }
    }

    public int saveAnalysis(int userId, String targetRole, String analysisJson, String modelUsed) {
        String sql = "INSERT INTO profile_analysis_history (user_id, target_role, analysis_json, model_used, status) "
                   + "VALUES (?, ?, ?, ?, 'COMPLETED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setString(2, targetRole != null ? targetRole : "Career Track");
            ps.setString(3, analysisJson);
            ps.setString(4, modelUsed != null ? modelUsed : "llama-3.3-70b-versatile");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save profile analysis for user " + userId, e);
        }
        return -1;
    }

    public AIAnalysisResult getLatestAnalysisByUserId(int userId) {
        String sql = "SELECT analysis_id, user_id, target_role, analysis_json, model_used, status, created_at "
                   + "FROM profile_analysis_history "
                   + "WHERE user_id = ? "
                   + "ORDER BY created_at DESC, analysis_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int analysisId = rs.getInt("analysis_id");
                    String targetRole = rs.getString("target_role");
                    String json = rs.getString("analysis_json");
                    String modelUsed = rs.getString("model_used");
                    String status = rs.getString("status");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    AIAnalysisResult res = AIAnalysisResult.fromJson(json, userId, targetRole, modelUsed);
                    res.setAnalysisId(analysisId);
                    res.setStatus(status);
                    res.setCreatedAt(createdAt);
                    return res;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch latest analysis for user " + userId, e);
        }
        return null;
    }

    public List<AIAnalysisResult> getAnalysisHistory(int userId) {
        List<AIAnalysisResult> list = new ArrayList<>();
        String sql = "SELECT analysis_id, user_id, target_role, analysis_json, model_used, status, created_at "
                   + "FROM profile_analysis_history "
                   + "WHERE user_id = ? "
                   + "ORDER BY created_at DESC LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AIAnalysisResult res = AIAnalysisResult.fromJson(
                        rs.getString("analysis_json"),
                        userId,
                        rs.getString("target_role"),
                        rs.getString("model_used")
                    );
                    res.setAnalysisId(rs.getInt("analysis_id"));
                    res.setStatus(rs.getString("status"));
                    res.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(res);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch analysis history for user " + userId, e);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // System Settings for deployment & Groq API Key storage
    // -------------------------------------------------------------------------

    public String getSetting(String key) {
        if (key == null) return null;
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to read system setting: " + key, e);
        }
        return null;
    }

    public boolean setSetting(String key, String value) {
        if (key == null || key.trim().isEmpty()) return false;
        String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) "
                   + "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), updated_at = CURRENT_TIMESTAMP";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key.trim());
            ps.setString(2, value != null ? value.trim() : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save system setting: " + key, e);
            return false;
        }
    }
}
