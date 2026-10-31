package com.kasnet.dao;

import com.kasnet.model.PlatformCertificate;
import com.kasnet.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlatformCertificateDAO {

    public List<PlatformCertificate> getByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM platform_certificates WHERE user_id = ? ORDER BY achievement_date DESC, certificate_id DESC";
        List<PlatformCertificate> certificates = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    certificates.add(mapRow(result));
                }
            }
        }
        return certificates;
    }

    public List<PlatformCertificate> getAllWithStudent() throws SQLException {
        String sql = "SELECT c.*, CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, '')) AS student_name, "
                + "u.email AS student_email FROM platform_certificates c "
                + "LEFT JOIN users u ON c.user_id = u.user_id "
                + "ORDER BY c.achievement_date DESC, c.certificate_id DESC";
        List<PlatformCertificate> certificates = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                PlatformCertificate certificate = mapRow(result);
                String name = result.getString("student_name");
                certificate.setStudentName(name == null || name.trim().isEmpty()
                        ? "Student #" + certificate.getUserId() : name.trim());
                certificate.setStudentEmail(result.getString("student_email"));
                certificates.add(certificate);
            }
        }
        return certificates;
    }

    public boolean add(PlatformCertificate certificate) throws SQLException {
        String sql = "INSERT INTO platform_certificates "
                + "(user_id, exam_name, score_obtained, total_score, achievement_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, certificate.getUserId());
            statement.setString(2, certificate.getExamName());
            statement.setBigDecimal(3, certificate.getScoreObtained());
            statement.setBigDecimal(4, certificate.getTotalScore());
            statement.setDate(5, certificate.getAchievementDate());
            if (statement.executeUpdate() != 1) {
                return false;
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    certificate.setCertificateId(keys.getInt(1));
                }
            }
            return true;
        }
    }

    public boolean delete(int certificateId, int userId) throws SQLException {
        String sql = "DELETE FROM platform_certificates WHERE certificate_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, certificateId);
            statement.setInt(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private PlatformCertificate mapRow(ResultSet result) throws SQLException {
        PlatformCertificate certificate = new PlatformCertificate();
        certificate.setCertificateId(result.getInt("certificate_id"));
        certificate.setUserId(result.getInt("user_id"));
        certificate.setExamName(result.getString("exam_name"));
        certificate.setScoreObtained(result.getBigDecimal("score_obtained"));
        certificate.setTotalScore(result.getBigDecimal("total_score"));
        certificate.setAchievementDate(result.getDate("achievement_date"));
        return certificate;
    }
}
