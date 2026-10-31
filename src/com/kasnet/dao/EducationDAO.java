package com.kasnet.dao;

import com.kasnet.model.Education;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EducationDAO {

    public List<Education> getEducationByUserId(int userId) {
        List<Education> list = new ArrayList<>();
        String sql = "SELECT * FROM education_details WHERE user_id = ? ORDER BY edu_id ASC";
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

    public Education getEducationByLevel(int userId, String level) {
        String sql = "SELECT * FROM education_details WHERE user_id = ? AND level = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, level);
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

    public Education getEducationById(int eduId, int userId) {
        String sql = "SELECT * FROM education_details WHERE edu_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eduId);
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

    public boolean saveOrUpdateEducation(Education edu) {
        String sql = "INSERT INTO education_details (user_id, level, school_college_name, board_university, stream, passing_year, percentage, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, NOW()) " +
                     "ON DUPLICATE KEY UPDATE school_college_name = VALUES(school_college_name), board_university = VALUES(board_university), " +
                     "stream = VALUES(stream), passing_year = VALUES(passing_year), percentage = VALUES(percentage), updated_at = NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, edu.getUserId());
            ps.setString(2, edu.getLevel());
            ps.setString(3, edu.getSchoolCollegeName());
            ps.setString(4, edu.getBoardUniversity());
            ps.setString(5, edu.getStream());
            ps.setInt(6, edu.getPassingYear());
            ps.setDouble(7, edu.getPercentage());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEducation(int eduId, int userId) {
        String sql = "DELETE FROM education_details WHERE edu_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eduId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Education mapRow(ResultSet rs) throws SQLException {
        Education e = new Education();
        e.setEduId(rs.getInt("edu_id"));
        e.setUserId(rs.getInt("user_id"));
        e.setLevel(rs.getString("level"));
        e.setSchoolCollegeName(rs.getString("school_college_name"));
        e.setBoardUniversity(rs.getString("board_university"));
        e.setStream(rs.getString("stream"));
        e.setPassingYear(rs.getInt("passing_year"));
        e.setPercentage(rs.getDouble("percentage"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        e.setUpdatedAt(rs.getTimestamp("updated_at"));
        return e;
    }
}
