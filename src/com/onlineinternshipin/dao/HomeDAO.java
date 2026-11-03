package com.onlineinternshipin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.onlineinternshipin.model.HomeStats;
import com.onlineinternshipin.utility.DBConnection;

public class HomeDAO {

    public HomeStats gethomeStats(int studentId) throws SQLException {
        HomeStats stats = new HomeStats();

        String assignSql = "SELECT COUNT(DISTINCT sad.student_assignment_id) AS total_assignments, "
                + "COUNT(DISTINCT CASE WHEN LOWER(TRIM(COALESCE(sad.assign_status, ''))) = 'completed' "
                + "THEN sad.student_assignment_id END) AS completed_assignments, "
                + "COUNT(DISTINCT CASE WHEN LOWER(TRIM(COALESCE(sad.assign_status, ''))) <> 'completed' "
                + "THEN sad.student_assignment_id END) AS pending_assignments "
                + "FROM student_assignment_data sad "
                + "JOIN assignment a ON a.assign_id = sad.assignment_master_id "
                + "WHERE sad.student_id = ? AND a.is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(assignSql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalAssignments(rs.getInt("total_assignments"));
                    stats.setCompletedAssignments(rs.getInt("completed_assignments"));
                    stats.setPendingAssignments(rs.getInt("pending_assignments"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
}
