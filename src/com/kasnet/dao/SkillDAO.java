package com.kasnet.dao;

import com.kasnet.model.Skill;
import com.kasnet.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@code skills} table.
 *
 * <p>Supports individual CRUD operations as well as a bulk
 * {@link #saveSkillsOfType(int, String, List)} that atomically
 * replaces all skills of a given type in a single transaction.
 */
public class SkillDAO {

    private static final Logger LOGGER = Logger.getLogger(SkillDAO.class.getName());

    // -------------------------------------------------------------------------
    // SELECT: All skills for a user
    // -------------------------------------------------------------------------

    /**
     * Returns all skill records for the specified user.
     *
     * @param userId the owning user's primary key
     * @return a (possibly empty) list of {@link Skill} objects
     */
    public List<Skill> getSkillsByUserId(int userId) {
        final String sql =
                "SELECT skill_id, user_id, skill_name, skill_type, proficiency, created_at "
                + "FROM skills WHERE user_id = ? ORDER BY skill_type, skill_name";

        List<Skill> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs   = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToSkill(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getSkillsByUserId: failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // SELECT: Skills filtered by type
    // -------------------------------------------------------------------------

    /**
     * Returns only the skills of a specific type (Technical, Soft, Domain)
     * for the given user.
     *
     * @param userId    the owning user's primary key
     * @param skillType the skill-type filter value
     * @return a (possibly empty) filtered list of {@link Skill} objects
     */
    public List<Skill> getSkillsByType(int userId, String skillType) {
        final String sql =
                "SELECT skill_id, user_id, skill_name, skill_type, proficiency, created_at "
                + "FROM skills WHERE user_id = ? AND skill_type = ? ORDER BY skill_name";

        List<Skill> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(   1, userId);
            ps.setString(2, skillType);
            rs   = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToSkill(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getSkillsByType: failed for userId=" + userId
                    + ", skillType=" + skillType, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // INSERT: Add a single skill
    // -------------------------------------------------------------------------

    /**
     * Inserts a single skill record and populates the generated primary key
     * back into the supplied {@link Skill} object.
     *
     * @param skill the skill to persist
     * @return {@code true} if the row was inserted successfully
     */
    public boolean addSkill(Skill skill) {
        final String sql =
                "INSERT INTO skills (user_id, skill_name, skill_type, created_at) "
                + "VALUES (?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(   1, skill.getUserId());
            ps.setString(2, skill.getSkillName());
            ps.setString(3, skill.getSkillType());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    skill.setSkillId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "addSkill: failed for userId=" + skill.getUserId()
                    + ", skillName=" + skill.getSkillName(), e);
        } finally {
            closeResources(generatedKeys, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // DELETE: Remove a single skill (ownership-verified)
    // -------------------------------------------------------------------------

    /**
     * Deletes the skill identified by {@code skillId} only when it belongs
     * to {@code userId}.
     *
     * @param skillId the primary key of the skill record
     * @param userId  the expected owning user
     * @return {@code true} if exactly one row was deleted
     */
    public boolean deleteSkill(int skillId, int userId) {
        final String sql = "DELETE FROM skills WHERE skill_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, skillId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteSkill: failed for skillId=" + skillId
                    + ", userId=" + userId, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // SELECT: Skill by ID (ownership-verified)
    // -------------------------------------------------------------------------

    public Skill getSkillById(int skillId, int userId) {
        final String sql =
                "SELECT skill_id, user_id, skill_name, skill_type, proficiency, created_at "
                + "FROM skills WHERE skill_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(1, skillId);
            ps.setInt(2, userId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return mapRowToSkill(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "getSkillById: failed for skillId=" + skillId + ", userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // UPDATE: Modify an existing skill (ownership-verified)
    // -------------------------------------------------------------------------

    public boolean updateSkill(Skill skill) {
        if (skill == null || skill.getSkillId() <= 0 || skill.getUserId() <= 0) return false;

        final String sql =
                "UPDATE skills SET skill_name = ?, skill_type = ? "
                + "WHERE skill_id = ? AND user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, skill.getSkillName().trim());
            ps.setString(2, skill.getSkillType().trim());
            ps.setInt(   3, skill.getSkillId());
            ps.setInt(   4, skill.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "updateSkill: failed for skillId=" + skill.getSkillId(), e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // CHECK: Skill uniqueness per user
    // -------------------------------------------------------------------------

    public boolean skillExists(int userId, String skillName, String skillType, int excludeSkillId) {
        if (skillName == null || skillName.trim().isEmpty()) return false;

        final String sql =
                "SELECT COUNT(*) FROM skills WHERE user_id = ? AND LOWER(skill_name) = LOWER(?) AND skill_id <> ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(   1, userId);
            ps.setString(2, skillName.trim());
            ps.setInt(   3, excludeSkillId);
            rs   = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "skillExists check failed for userId=" + userId, e);
        } finally {
            closeResources(rs, ps, conn);
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // DELETE: Remove all skills of a given type for a user
    // -------------------------------------------------------------------------

    /**
     * Deletes <em>all</em> skill records of the specified type for the given
     * user. Typically called as the first step of a bulk-replace operation.
     *
     * @param userId    the owning user's primary key
     * @param skillType the skill-type category to clear
     * @return {@code true} if the statement executed without error
     *         (even if zero rows were deleted)
     */
    public boolean deleteAllSkillsOfType(int userId, String skillType) {
        final String sql = "DELETE FROM skills WHERE user_id = ? AND skill_type = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setInt(   1, userId);
            ps.setString(2, skillType);
            ps.executeUpdate();   // success even if 0 rows affected
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deleteAllSkillsOfType: failed for userId=" + userId
                    + ", skillType=" + skillType, e);
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    // -------------------------------------------------------------------------
    // BULK-SAVE: Atomic delete-then-insert for a skill type
    // -------------------------------------------------------------------------

    /**
     * Atomically replaces all skills of {@code skillType} for the given user
     * with the supplied list of skill names.
     *
     * <p>The operation runs inside a single transaction: the existing skills of
     * that type are deleted first, then the new batch is inserted.  If any
     * insert fails the entire transaction is rolled back.
     *
     * @param userId      the owning user's primary key
     * @param skillType   the skill-type category (Technical, Soft, Domain)
     * @param skillNames  the list of skill names to insert; may be empty
     *                    (which effectively clears the section)
     * @return {@code true} if the transaction committed successfully
     */
    public boolean saveSkillsOfType(int userId, String skillType,
                                    List<String> skillNames) {
        final String deleteSql = "DELETE FROM skills WHERE user_id = ? AND skill_type = ?";
        final String insertSql =
                "INSERT INTO skills (user_id, skill_name, skill_type, created_at) "
                + "VALUES (?, ?, ?, NOW())";

        Connection conn = null;
        PreparedStatement deletePs = null;
        PreparedStatement insertPs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 1: delete existing skills of this type
            deletePs = conn.prepareStatement(deleteSql);
            deletePs.setInt(   1, userId);
            deletePs.setString(2, skillType);
            deletePs.executeUpdate();

            // Step 2: batch-insert the new skills
            if (skillNames != null && !skillNames.isEmpty()) {
                insertPs = conn.prepareStatement(insertSql);
                for (String name : skillNames) {
                    if (name == null || name.trim().isEmpty()) continue;
                    insertPs.setInt(   1, userId);
                    insertPs.setString(2, name.trim());
                    insertPs.setString(3, skillType);
                    insertPs.addBatch();
                }
                insertPs.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "saveSkillsOfType: failed for userId=" + userId
                    + ", skillType=" + skillType, e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "saveSkillsOfType: rollback failed", ex);
                }
            }
            return false;
        } finally {
            closeResources(null, deletePs, null);
            closeResources(null, insertPs, null);
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); }
                catch (SQLException ex) { LOGGER.log(Level.WARNING, "saveSkillsOfType: close failed", ex); }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helper: map a ResultSet row to a Skill object
    // -------------------------------------------------------------------------

    private Skill mapRowToSkill(ResultSet rs) throws SQLException {
        Skill s = new Skill();
        s.setSkillId(rs.getInt("skill_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setSkillName(rs.getString("skill_name"));
        s.setSkillType(rs.getString("skill_type"));
        s.setProficiency(rs.getString("proficiency"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }

    // -------------------------------------------------------------------------
    // Helper: close JDBC resources safely
    // -------------------------------------------------------------------------

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs   != null) { try { rs.close();   } catch (SQLException ex) { LOGGER.log(Level.WARNING, "Failed to close ResultSet",        ex); } }
        if (ps   != null) { try { ps.close();   } catch (SQLException ex) { LOGGER.log(Level.WARNING, "Failed to close PreparedStatement", ex); } }
        if (conn != null) { try { conn.close(); } catch (SQLException ex) { LOGGER.log(Level.WARNING, "Failed to close Connection",        ex); } }
    }
}
