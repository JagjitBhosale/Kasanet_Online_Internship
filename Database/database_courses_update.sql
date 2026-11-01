-- =============================================================
--  Course Video Lecture System — Database Schema Update
--  Run AFTER the main database.sql has been executed.
--  Database: onlineinternship_db
-- =============================================================

USE `onlineinternship_db`;

SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================
--  TABLE: courses
-- =============================================================
DROP TABLE IF EXISTS `video_access_logs`;
DROP TABLE IF EXISTS `enrollments`;
DROP TABLE IF EXISTS `courses`;

CREATE TABLE IF NOT EXISTS `courses` (
  `id`              INT            NOT NULL AUTO_INCREMENT,
  `title`           VARCHAR(255)   NOT NULL,
  `slug`            VARCHAR(255)   DEFAULT NULL,
  `description`     TEXT           DEFAULT NULL,
  `thumbnail_url`   VARCHAR(500)   DEFAULT NULL,
  `instructor_name` VARCHAR(255)   NOT NULL,
  `category`        VARCHAR(100)   DEFAULT 'Engineering',
  `difficulty`      ENUM('Beginner','Intermediate','Advanced') DEFAULT 'Beginner',
  `duration_weeks`  INT            DEFAULT 6,
  `total_lessons`   INT            DEFAULT 0,
  `status`          ENUM('DRAFT','PUBLISHED','ARCHIVED') DEFAULT 'PUBLISHED',
  `created_at`      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_course_status` (`status`),
  INDEX `idx_course_slug`   (`slug`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Courses available in the learning platform';

-- =============================================================
--  TABLE: enrollments
-- =============================================================
CREATE TABLE IF NOT EXISTS `enrollments` (
  `id`                    INT        NOT NULL AUTO_INCREMENT,
  `user_id`               INT        NOT NULL,
  `course_id`             INT        NOT NULL,
  `progress_percentage`   INT        DEFAULT 0,
  `completed_lessons`     INT        DEFAULT 0,
  `enrolled_at`           TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed_at`          TIMESTAMP  NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_enrollment` (`user_id`, `course_id`),
  INDEX `idx_enr_user`   (`user_id`),
  INDEX `idx_enr_course` (`course_id`),
  CONSTRAINT `fk_enr_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_enr_course`
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Student enrollments in courses';

-- =============================================================
--  TABLE: video_access_logs
-- =============================================================
CREATE TABLE IF NOT EXISTS `video_access_logs` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `user_id`     INT          NOT NULL,
  `lecture_id`  INT          NOT NULL,
  `ip_address`  VARCHAR(45)  DEFAULT NULL,
  `user_agent`  VARCHAR(500) DEFAULT NULL,
  `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_val_user`    (`user_id`),
  INDEX `idx_val_lecture` (`lecture_id`),
  INDEX `idx_val_ip_time` (`ip_address`, `created_at`),
  CONSTRAINT `fk_val_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Audit log of every video access request';

-- =============================================================
--  ALTER: lectures — add course-related columns
--  NOTE: Only run these if the columns do not already exist.
--  If you get "Duplicate column" errors, the columns already exist.
-- =============================================================
ALTER TABLE `lectures`
  ADD COLUMN IF NOT EXISTS `course_id`             INT          DEFAULT NULL AFTER `lecture_id`,
  ADD COLUMN IF NOT EXISTS `cloudinary_public_id`  VARCHAR(500) DEFAULT NULL AFTER `video_url`,
  ADD COLUMN IF NOT EXISTS `duration_seconds`      INT          DEFAULT 0   AFTER `cloudinary_public_id`,
  ADD COLUMN IF NOT EXISTS `is_preview`            TINYINT(1)   DEFAULT 0   AFTER `duration_seconds`,
  ADD COLUMN IF NOT EXISTS `status`                ENUM('DRAFT','PUBLISHED') DEFAULT 'PUBLISHED' AFTER `is_preview`;

-- Add index and FK only if they don't exist (ignore errors)
ALTER TABLE `lectures`
  ADD INDEX `idx_lec_course` (`course_id`);

ALTER TABLE `lectures`
  ADD CONSTRAINT `fk_lec_course`
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- =============================================================
--  Rename lectures PK to match Java DAO expectations
--  The LectureDAO.mapRow() uses rs.getInt("id")
-- =============================================================
-- Create an alias view or rename the column:
ALTER TABLE `lectures` CHANGE COLUMN `lecture_id` `id` INT NOT NULL AUTO_INCREMENT;

-- =============================================================
--  ALTER: lecture_progress — align column names with Java DAO
--  The LectureDAO expects: completed, progress_seconds, last_watched_at
-- =============================================================
ALTER TABLE `lecture_progress`
  ADD COLUMN IF NOT EXISTS `progress_seconds` INT DEFAULT 0 AFTER `watch_progress_percent`;

-- Rename columns to match Java DAO expectations
ALTER TABLE `lecture_progress` CHANGE COLUMN `is_completed` `completed` TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE `lecture_progress` CHANGE COLUMN `last_watched` `last_watched_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Update FK reference after lectures PK rename
ALTER TABLE `lecture_progress` DROP FOREIGN KEY `fk_lp_lecture`;
ALTER TABLE `lecture_progress`
  ADD CONSTRAINT `fk_lp_lecture`
    FOREIGN KEY (`lecture_id`) REFERENCES `lectures` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Update video_access_logs FK to reference renamed lectures PK
ALTER TABLE `video_access_logs`
  ADD CONSTRAINT `fk_val_lecture`
    FOREIGN KEY (`lecture_id`) REFERENCES `lectures` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================
--  SAMPLE DATA: Courses
-- =============================================================
INSERT INTO `courses` (`title`, `slug`, `description`, `thumbnail_url`, `instructor_name`, `category`, `difficulty`, `duration_weeks`, `total_lessons`, `status`)
VALUES
  (
    'Java Web Development Masterclass',
    'java-web-development-masterclass',
    'Complete guide to building web applications with Java EE, Servlets, JSP, and MVC architecture. Learn from scratch to deployment.',
    NULL,
    'Prof. Rahul Sharma',
    'Engineering',
    'Beginner',
    8,
    3,
    'PUBLISHED'
  ),
  (
    'Database Design & SQL Mastery',
    'database-design-sql-mastery',
    'Master relational database design, MySQL queries, optimization, and JDBC integration for Java applications.',
    NULL,
    'Prof. Priya Mehta',
    'Engineering',
    'Intermediate',
    6,
    0,
    'PUBLISHED'
  ),
  (
    'Full Stack Project Development',
    'full-stack-project-development',
    'Build a complete full-stack application from requirements to deployment. Covers frontend, backend, database, and DevOps.',
    NULL,
    'Prof. Rahul Sharma',
    'Engineering',
    'Advanced',
    10,
    0,
    'DRAFT'
  );

-- Link existing lectures to the first course
UPDATE `lectures` SET `course_id` = 1, `status` = 'PUBLISHED', `is_preview` = 0
WHERE `id` IN (1, 2, 3);

-- Make the first lecture a free preview
UPDATE `lectures` SET `is_preview` = 1 WHERE `id` = 1;

-- =============================================================
--  END OF UPDATE
-- =============================================================
