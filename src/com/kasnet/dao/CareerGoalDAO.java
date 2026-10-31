package com.kasnet.dao;

import com.kasnet.model.CareerGoal;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the career_goals table.
 * Enforces ownership on every query and mutation.
 */
public class CareerGoalDAO {

    private static final Logger LOGGER = Logger.getLogger(CareerGoalDAO.class.getName());

    public CareerGoal getCareerGoalByUserId(int userId) {
        final String sql =
                "SELECT goal_id, user_id, target_role, target_role_other, "
                + "preferred_domain, preferred_domain_other, career_objective, created_at, updated_at "
                + "FROM career_goals WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getCareerGoalByUserId: failed for userId=" + userId, e);
        }
        return null;
    }

    public boolean saveOrUpdateCareerGoal(CareerGoal goal) {
        if (goal == null || goal.getUserId() <= 0) {
            return false;
        }

        final String sql =
                "INSERT INTO career_goals "
                + "(user_id, target_role, target_role_other, preferred_domain, preferred_domain_other, career_objective, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW()) "
                + "ON DUPLICATE KEY UPDATE "
                + "target_role = VALUES(target_role), "
                + "target_role_other = VALUES(target_role_other), "
                + "preferred_domain = VALUES(preferred_domain), "
                + "preferred_domain_other = VALUES(preferred_domain_other), "
                + "career_objective = VALUES(career_objective), "
                + "updated_at = NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, goal.getUserId());
            ps.setString(2, goal.getTargetRole());
            ps.setString(3, goal.getTargetRoleOther());
            ps.setString(4, goal.getPreferredDomain());
            ps.setString(5, goal.getPreferredDomainOther());
            ps.setString(6, goal.getCareerObjective());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        goal.setGoalId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "saveOrUpdateCareerGoal: failed for userId=" + goal.getUserId(), e);
        }
        return false;
    }

    public boolean deleteCareerGoal(int userId) {
        final String sql = "DELETE FROM career_goals WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteCareerGoal: failed for userId=" + userId, e);
        }
        return false;
    }

    private CareerGoal mapRow(ResultSet rs) throws SQLException {
        CareerGoal g = new CareerGoal();
        g.setGoalId(rs.getInt("goal_id"));
        g.setUserId(rs.getInt("user_id"));
        g.setTargetRole(rs.getString("target_role"));
        g.setTargetRoleOther(rs.getString("target_role_other"));
        g.setPreferredDomain(rs.getString("preferred_domain"));
        g.setPreferredDomainOther(rs.getString("preferred_domain_other"));
        g.setCareerObjective(rs.getString("career_objective"));
        g.setCreatedAt(rs.getTimestamp("created_at"));
        g.setUpdatedAt(rs.getTimestamp("updated_at"));
        return g;
    }
}
