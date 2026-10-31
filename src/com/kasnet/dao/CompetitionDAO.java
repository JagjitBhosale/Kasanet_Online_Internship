package com.kasnet.dao;

import com.kasnet.model.Competition;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompetitionDAO {

    public List<Competition> getCompetitionsByUserId(int userId) {
        List<Competition> list = new ArrayList<>();
        String sql = "SELECT * FROM intercollege_competitions WHERE user_id = ? ORDER BY competition_id DESC";
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

    public Competition getCompetitionById(int competitionId, int userId) {
        String sql = "SELECT * FROM intercollege_competitions WHERE competition_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, competitionId);
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

    public boolean addCompetition(Competition c) {
        String sql = "INSERT INTO intercollege_competitions (user_id, competition_name, college_organizer, event_date, project_name, position_result, description, team_name, certificate_path, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getCompetitionName());
            ps.setString(3, c.getCollegeOrganizer());
            ps.setDate(4, c.getEventDate());
            ps.setString(5, c.getProjectName());
            ps.setString(6, c.getPositionResult());
            ps.setString(7, c.getDescription());
            ps.setString(8, c.getTeamName());
            ps.setString(9, c.getCertificatePath());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        c.setCompetitionId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCompetition(Competition c) {
        String sql = "UPDATE intercollege_competitions SET competition_name = ?, college_organizer = ?, event_date = ?, project_name = ?, position_result = ?, description = ?, team_name = ?, certificate_path = ?, updated_at = NOW() " +
                     "WHERE competition_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCompetitionName());
            ps.setString(2, c.getCollegeOrganizer());
            ps.setDate(3, c.getEventDate());
            ps.setString(4, c.getProjectName());
            ps.setString(5, c.getPositionResult());
            ps.setString(6, c.getDescription());
            ps.setString(7, c.getTeamName());
            ps.setString(8, c.getCertificatePath());
            ps.setInt(9, c.getCompetitionId());
            ps.setInt(10, c.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCompetition(int competitionId, int userId) {
        String sql = "DELETE FROM intercollege_competitions WHERE competition_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, competitionId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Competition mapRow(ResultSet rs) throws SQLException {
        Competition c = new Competition();
        c.setCompetitionId(rs.getInt("competition_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCompetitionName(rs.getString("competition_name"));
        c.setCollegeOrganizer(rs.getString("college_organizer"));
        c.setEventDate(rs.getDate("event_date"));
        c.setProjectName(rs.getString("project_name"));
        c.setPositionResult(rs.getString("position_result"));
        c.setDescription(rs.getString("description"));
        c.setTeamName(rs.getString("team_name"));
        c.setCertificatePath(rs.getString("certificate_path"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
