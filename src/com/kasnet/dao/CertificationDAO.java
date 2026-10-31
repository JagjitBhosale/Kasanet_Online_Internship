package com.kasnet.dao;

import com.kasnet.model.Certification;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificationDAO {

    public List<Certification> getCertificationsByUserId(int userId) {
        List<Certification> list = new ArrayList<>();
        String sql = "SELECT * FROM certifications WHERE user_id = ? ORDER BY cert_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Certification getCertificationById(int certId, int userId) {
        String sql = "SELECT * FROM certifications WHERE cert_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, certId);
            ps.setInt(2, userId);
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

    public boolean addCertification(Certification c) {
        String sql = "INSERT INTO certifications (user_id, cert_name, provider, technology, cert_id_number, issue_date, expiry_date, credential_url, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getCertName());
            ps.setString(3, c.getProvider());
            ps.setString(4, c.getTechnology());
            ps.setString(5, c.getCertIdNumber());
            ps.setDate(6, c.getIssueDate());
            ps.setDate(7, c.getExpiryDate());
            ps.setString(8, c.getCredentialUrl());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        c.setCertId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCertification(Certification c) {
        String sql = "UPDATE certifications SET cert_name = ?, provider = ?, technology = ?, cert_id_number = ?, issue_date = ?, expiry_date = ?, credential_url = ?, updated_at = NOW() " +
                     "WHERE cert_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCertName());
            ps.setString(2, c.getProvider());
            ps.setString(3, c.getTechnology());
            ps.setString(4, c.getCertIdNumber());
            ps.setDate(5, c.getIssueDate());
            ps.setDate(6, c.getExpiryDate());
            ps.setString(7, c.getCredentialUrl());
            ps.setInt(8, c.getCertId());
            ps.setInt(9, c.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCertification(int certId, int userId) {
        String sql = "DELETE FROM certifications WHERE cert_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, certId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCertificationByAdmin(int certId) {
        String sql = "DELETE FROM certifications WHERE cert_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, certId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Certification> getAllCertificationsWithStudent() {
        List<Certification> list = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, '')) AS student_name, "
                   + "u.email AS student_email "
                   + "FROM certifications c "
                   + "LEFT JOIN users u ON c.user_id = u.user_id "
                   + "ORDER BY c.created_at DESC, c.cert_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Certification c = mapRow(rs);
                try {
                    String sName = rs.getString("student_name");
                    c.setStudentName(sName != null && !sName.trim().isEmpty() ? sName.trim() : "Student #" + c.getUserId());
                    c.setStudentEmail(rs.getString("student_email"));
                } catch (SQLException ignore) {}
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Certification mapRow(ResultSet rs) throws SQLException {
        Certification c = new Certification();
        c.setCertId(rs.getInt("cert_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCertName(rs.getString("cert_name"));
        c.setProvider(rs.getString("provider"));
        c.setTechnology(rs.getString("technology"));
        c.setCertIdNumber(rs.getString("cert_id_number"));
        c.setIssueDate(rs.getDate("issue_date"));
        c.setExpiryDate(rs.getDate("expiry_date"));
        c.setCredentialUrl(rs.getString("credential_url"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
