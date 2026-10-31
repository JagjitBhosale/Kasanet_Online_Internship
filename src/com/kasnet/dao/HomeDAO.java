package com.kasnet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kasnet.model.HomeStats;
import com.kasnet.util.DBConnection;

public class HomeDAO {

    public HomeStats gethomeStats(int userId) {
        HomeStats stats = new HomeStats();

        // 1. Get user name & profile completion
        String userSql = "SELECT u.first_name, u.last_name, p.profile_completion " +
                         "FROM users u " +
                         "LEFT JOIN student_profiles p ON u.user_id = p.user_id " +
                         "WHERE u.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    stats.setProfileCompletion(rs.getInt("profile_completion"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Assignment stats
        String assignSql = "SELECT " +
                           "(SELECT COUNT(*) FROM assignments WHERE is_active = 1) AS total_assignments, " +
                           "(SELECT COUNT(*) FROM assignment_submissions WHERE user_id = ? AND status = 'Submitted') AS submitted_assignments, " +
                           "(SELECT COUNT(*) FROM assignment_submissions WHERE user_id = ? AND status = 'Evaluated') AS evaluated_assignments";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(assignSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_assignments");
                    int submitted = rs.getInt("submitted_assignments");
                    int evaluated = rs.getInt("evaluated_assignments");
                    stats.setTotalAssignments(total);
                    stats.setSubmittedAssignments(submitted);
                    stats.setEvaluatedAssignments(evaluated);
                    stats.setPendingAssignments(Math.max(0, total - (submitted + evaluated)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. Lecture stats
        String lectureSql = "SELECT " +
                            "(SELECT COUNT(*) FROM lectures WHERE is_active = 1) AS total_lectures, " +
                            "(SELECT COUNT(*) FROM lecture_progress WHERE user_id = ? AND is_completed = 1) AS completed_lectures";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(lectureSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalLectures(rs.getInt("total_lectures"));
                    stats.setCompletedLectures(rs.getInt("completed_lectures"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 4. Portfolio items: Certifications, Projects, Hackathons, Competitions
        String portfolioSql = "SELECT " +
                              "(SELECT COUNT(*) FROM certifications WHERE user_id = ?) AS certs_count, " +
                              "(SELECT COUNT(*) FROM projects WHERE user_id = ?) AS projects_count, " +
                              "(SELECT COUNT(*) FROM hackathons WHERE user_id = ?) AS hackathons_count, " +
                              "(SELECT COUNT(*) FROM intercollege_competitions WHERE user_id = ?) AS comp_count";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(portfolioSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setCertificationsCount(rs.getInt("certs_count"));
                    stats.setProjectsCount(rs.getInt("projects_count"));
                    stats.setHackathonsCount(rs.getInt("hackathons_count"));
                    stats.setCompetitionsCount(rs.getInt("comp_count"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
}
