-- ==============================================================================
-- Migration: V002__create_engineering_year_marks.sql
-- Description: Creates engineering_year_marks table for storing year-wise
--              engineering performance (First Year, Second Year, Third Year, Final Year)
--              linked directly to the production 'student' table.
-- Target: MySQL 5.7+ / 8.0+
-- Safety: Additive only. Preserves all existing tables and data.
-- ==============================================================================

CREATE TABLE IF NOT EXISTS `engineering_year_marks` (
    `mark_id` INT(11) NOT NULL AUTO_INCREMENT,
    `user_id` INT(11) NOT NULL,
    `year_level` VARCHAR(50) NOT NULL,
    `score_type` VARCHAR(20) NOT NULL DEFAULT 'Percentage',
    `score_value` DECIMAL(5,2) NOT NULL,
    `passing_year` INT(11) DEFAULT NULL,
    `college_name` VARCHAR(255) DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`mark_id`),
    UNIQUE KEY `uk_student_year` (`user_id`, `year_level`),
    KEY `idx_eng_marks_user` (`user_id`),
    CONSTRAINT `fk_eng_marks_student` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ==============================================================================
-- Rollback Instructions:
-- DROP TABLE IF EXISTS `engineering_year_marks`;
-- ==============================================================================
