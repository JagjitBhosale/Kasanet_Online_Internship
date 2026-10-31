package com.kasnet.dao;

import com.kasnet.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ResumeAnalysisDAO - Database schema initialization and helper utility
 * for the Resume Analysis / Student Profile Information module.
 *
 * Ensures that the required tables (`career_goals`, `experience`) exist
 * and that `skills.skill_type` supports extended categories.
 */
public class ResumeAnalysisDAO {

    private static final Logger LOGGER = Logger.getLogger(ResumeAnalysisDAO.class.getName());
    private static volatile boolean schemaInitialized = false;

    /**
     * Idempotently verifies and creates missing tables for the module.
     */
    public static synchronized void ensureSchema() {
        if (schemaInitialized) {
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. career_goals table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `career_goals` ("
                + "  `goal_id`               INT          NOT NULL AUTO_INCREMENT,"
                + "  `user_id`               INT          NOT NULL,"
                + "  `target_role`           VARCHAR(100) NOT NULL,"
                + "  `target_role_other`     VARCHAR(100) DEFAULT NULL,"
                + "  `preferred_domain`      VARCHAR(100) DEFAULT NULL,"
                + "  `preferred_domain_other` VARCHAR(100) DEFAULT NULL,"
                + "  `career_objective`      TEXT         DEFAULT NULL,"
                + "  `created_at`            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  `updated_at`            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (`goal_id`),"
                + "  UNIQUE KEY `uq_cg_user` (`user_id`),"
                + "  INDEX `idx_cg_user` (`user_id`),"
                + "  CONSTRAINT `fk_cg_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) "
                + "    ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );

            // 2. experience table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `experience` ("
                + "  `experience_id`      INT          NOT NULL AUTO_INCREMENT,"
                + "  `user_id`            INT          NOT NULL,"
                + "  `company_name`       VARCHAR(200) NOT NULL,"
                + "  `role`               VARCHAR(100) NOT NULL,"
                + "  `employment_type`    VARCHAR(50)  NOT NULL DEFAULT 'Full-time',"
                + "  `technologies_used`  VARCHAR(500) DEFAULT NULL,"
                + "  `description`        TEXT         DEFAULT NULL,"
                + "  `start_date`         DATE         DEFAULT NULL,"
                + "  `end_date`           DATE         DEFAULT NULL,"
                + "  `is_current`         TINYINT(1)   NOT NULL DEFAULT 0,"
                + "  `created_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "  `updated_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "  PRIMARY KEY (`experience_id`),"
                + "  INDEX `idx_exp_user` (`user_id`),"
                + "  CONSTRAINT `fk_exp_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) "
                + "    ON DELETE CASCADE ON UPDATE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci"
            );

            // 3. Relax skills.skill_type to VARCHAR(50) so category names can be stored seamlessly
            try {
                stmt.executeUpdate("ALTER TABLE `skills` MODIFY COLUMN `skill_type` VARCHAR(50) NOT NULL");
            } catch (SQLException ignored) {
                // Ignore if already VARCHAR or insufficient permission
            }

            schemaInitialized = true;
            ProfileAnalysisDAO.ensureSchema();
            LOGGER.info("ResumeAnalysis schema verified successfully.");

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "ResumeAnalysisDAO: Schema verification encountered: " + e.getMessage());
        }
    }
}
