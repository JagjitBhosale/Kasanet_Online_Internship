package com.kasnet.dao;

import com.kasnet.model.Project;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    public List<Project> getProjectsByUserId(int userId) {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE user_id = ? ORDER BY project_id DESC";
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

    public Project getProjectById(int projectId, int userId) {
        String sql = "SELECT * FROM projects WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
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

    public boolean addProject(Project p) {
        String sql = "INSERT INTO projects (user_id, project_name, description, technologies_used, role, project_type, team_size, start_date, end_date, project_url, github_url, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getUserId());
            ps.setString(2, p.getProjectName());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getTechnologiesUsed());
            ps.setString(5, p.getRole());
            ps.setString(6, p.getProjectType());
            ps.setInt(7, p.getTeamSize());
            ps.setDate(8, p.getStartDate());
            ps.setDate(9, p.getEndDate());
            ps.setString(10, p.getProjectUrl());
            ps.setString(11, p.getGithubUrl());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setProjectId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProject(Project p) {
        String sql = "UPDATE projects SET project_name = ?, description = ?, technologies_used = ?, role = ?, project_type = ?, team_size = ?, start_date = ?, end_date = ?, project_url = ?, github_url = ?, updated_at = NOW() " +
                     "WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProjectName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getTechnologiesUsed());
            ps.setString(4, p.getRole());
            ps.setString(5, p.getProjectType());
            ps.setInt(6, p.getTeamSize());
            ps.setDate(7, p.getStartDate());
            ps.setDate(8, p.getEndDate());
            ps.setString(9, p.getProjectUrl());
            ps.setString(10, p.getGithubUrl());
            ps.setInt(11, p.getProjectId());
            ps.setInt(12, p.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteProject(int projectId, int userId) {
        String sql = "DELETE FROM projects WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Project mapRow(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setProjectId(rs.getInt("project_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setProjectName(rs.getString("project_name"));
        p.setDescription(rs.getString("description"));
        p.setTechnologiesUsed(rs.getString("technologies_used"));
        p.setRole(rs.getString("role"));
        p.setProjectType(rs.getString("project_type"));
        p.setTeamSize(rs.getInt("team_size"));
        p.setStartDate(rs.getDate("start_date"));
        p.setEndDate(rs.getDate("end_date"));
        p.setProjectUrl(rs.getString("project_url"));
        p.setGithubUrl(rs.getString("github_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }
}
