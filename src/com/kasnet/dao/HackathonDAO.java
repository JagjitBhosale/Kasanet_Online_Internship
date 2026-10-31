package com.kasnet.dao;

import com.kasnet.model.Hackathon;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HackathonDAO {

    public List<Hackathon> getHackathonsByUserId(int userId) {
        List<Hackathon> list = new ArrayList<>();
        String sql = "SELECT * FROM hackathons WHERE user_id = ? ORDER BY hackathon_id DESC";
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

    public Hackathon getHackathonById(int hackathonId, int userId) {
        String sql = "SELECT * FROM hackathons WHERE hackathon_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hackathonId);
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

    public boolean addHackathon(Hackathon h) {
        String sql = "INSERT INTO hackathons (user_id, hackathon_name, organizer, event_date, position_result, description, team_name, certificate_path, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, h.getUserId());
            ps.setString(2, h.getHackathonName());
            ps.setString(3, h.getOrganizer());
            ps.setDate(4, h.getEventDate());
            ps.setString(5, h.getPositionResult());
            ps.setString(6, h.getDescription());
            ps.setString(7, h.getTeamName());
            ps.setString(8, h.getCertificatePath());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        h.setHackathonId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateHackathon(Hackathon h) {
        String sql = "UPDATE hackathons SET hackathon_name = ?, organizer = ?, event_date = ?, position_result = ?, description = ?, team_name = ?, certificate_path = ?, updated_at = NOW() " +
                     "WHERE hackathon_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getHackathonName());
            ps.setString(2, h.getOrganizer());
            ps.setDate(3, h.getEventDate());
            ps.setString(4, h.getPositionResult());
            ps.setString(5, h.getDescription());
            ps.setString(7, h.getTeamName());
            ps.setString(8, h.getCertificatePath());
            ps.setInt(9, h.getHackathonId());
            ps.setInt(10, h.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteHackathon(int hackathonId, int userId) {
        String sql = "DELETE FROM hackathons WHERE hackathon_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hackathonId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Hackathon mapRow(ResultSet rs) throws SQLException {
        Hackathon h = new Hackathon();
        h.setHackathonId(rs.getInt("hackathon_id"));
        h.setUserId(rs.getInt("user_id"));
        h.setHackathonName(rs.getString("hackathon_name"));
        h.setOrganizer(rs.getString("organizer"));
        h.setEventDate(rs.getDate("event_date"));
        h.setPositionResult(rs.getString("position_result"));
        h.setDescription(rs.getString("description"));
        h.setTeamName(rs.getString("team_name"));
        h.setCertificatePath(rs.getString("certificate_path"));
        h.setCreatedAt(rs.getTimestamp("created_at"));
        h.setUpdatedAt(rs.getTimestamp("updated_at"));
        return h;
    }
}
