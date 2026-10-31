package com.kasnet.dao;

import com.kasnet.model.Achievement;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {

    public List<Achievement> getAchievementsByUserId(int userId) {
        List<Achievement> list = new ArrayList<>();
        String sql = "SELECT * FROM achievements WHERE user_id = ? ORDER BY achievement_id DESC";
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

    public Achievement getAchievementById(int achievementId, int userId) {
        String sql = "SELECT * FROM achievements WHERE achievement_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, achievementId);
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

    public boolean addAchievement(Achievement a) {
        String sql = "INSERT INTO achievements (user_id, achievement_type, title, description, technology, extra_activities, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getUserId());
            ps.setString(2, a.getAchievementType());
            ps.setString(3, a.getTitle());
            ps.setString(4, a.getDescription());
            ps.setString(5, a.getTechnology());
            ps.setString(6, a.getExtraActivities());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        a.setAchievementId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAchievement(Achievement a) {
        String sql = "UPDATE achievements SET achievement_type = ?, title = ?, description = ?, technology = ?, extra_activities = ?, updated_at = NOW() " +
                     "WHERE achievement_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAchievementType());
            ps.setString(2, a.getTitle());
            ps.setString(3, a.getDescription());
            ps.setString(4, a.getTechnology());
            ps.setString(5, a.getExtraActivities());
            ps.setInt(6, a.getAchievementId());
            ps.setInt(7, a.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAchievement(int achievementId, int userId) {
        String sql = "DELETE FROM achievements WHERE achievement_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, achievementId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Achievement mapRow(ResultSet rs) throws SQLException {
        Achievement a = new Achievement();
        a.setAchievementId(rs.getInt("achievement_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setAchievementType(rs.getString("achievement_type"));
        a.setTitle(rs.getString("title"));
        a.setDescription(rs.getString("description"));
        a.setTechnology(rs.getString("technology"));
        a.setExtraActivities(rs.getString("extra_activities"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));
        return a;
    }
}
