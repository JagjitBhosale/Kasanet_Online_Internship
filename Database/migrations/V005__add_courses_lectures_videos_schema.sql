-- =============================================================
--  Migration V005: Add Courses, Lectures, Videos & Assignment
--  Enhancement Schema
--
--  This migration adds the tables required by the Java code for:
--    1. Course management (courses)
--    2. Lecture/video management (lectures)
--    3. Student enrollment tracking (enrollments)
--    4. Lecture watch progress (lecture_progress)
--    5. Video access audit/rate-limiting (video_access_logs)
--    6. Assignment table enhancements (new columns)
--    7. Student assignment data FK linking (assignment_master_id)
--
--  Safe to run on an existing database — uses IF NOT EXISTS
--  and conditional ALTER TABLE checks.
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================
--  1. COURSES TABLE
--  Referenced by: CourseDAO.java, AdminCourseServlet.java
-- =============================================================
CREATE TABLE IF NOT EXISTS `courses` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `slug` VARCHAR(255) DEFAULT NULL,
  `description` TEXT,
  `thumbnail_url` VARCHAR(500) DEFAULT NULL,
  `instructor_name` VARCHAR(255) DEFAULT NULL,
  `category` VARCHAR(100) DEFAULT 'Engineering',
  `difficulty` VARCHAR(50) DEFAULT 'Beginner',
  `duration_weeks` INT NOT NULL DEFAULT 6,
  `total_lessons` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_course_slug` (`slug`),
  INDEX `idx_course_status` (`status`),
  INDEX `idx_course_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Course catalog for the learning platform';


-- =============================================================
--  2. LECTURES TABLE
--  Referenced by: LectureDAO.java, AdminCourseServlet.java
--  Stores Cloudinary public IDs (NOT URLs) for video assets
-- =============================================================
CREATE TABLE IF NOT EXISTS `lectures` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `course_id` INT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `lecture_number` INT NOT NULL DEFAULT 1,
  `cloudinary_public_id` VARCHAR(255) DEFAULT NULL,
  `duration_seconds` INT NOT NULL DEFAULT 0,
  `is_preview` TINYINT(1) NOT NULL DEFAULT 0,
  `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_lecture_course` (`course_id`),
  INDEX `idx_lecture_status` (`status`),
  INDEX `idx_lecture_order` (`course_id`, `lecture_number`),
  CONSTRAINT `fk_lecture_course` FOREIGN KEY (`course_id`)
    REFERENCES `courses`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Lectures/video lessons linked to courses';


-- =============================================================
--  3. ENROLLMENTS TABLE
--  Referenced by: EnrollmentDAO.java, CourseDAO.java
-- =============================================================
CREATE TABLE IF NOT EXISTS `enrollments` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `course_id` INT NOT NULL,
  `progress_percentage` INT NOT NULL DEFAULT 0,
  `completed_lessons` INT NOT NULL DEFAULT 0,
  `enrolled_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed_at` TIMESTAMP NULL DEFAULT NULL,
  UNIQUE KEY `uk_enrollment_user_course` (`user_id`, `course_id`),
  INDEX `idx_enrollment_user` (`user_id`),
  INDEX `idx_enrollment_course` (`course_id`),
  CONSTRAINT `fk_enrollment_course` FOREIGN KEY (`course_id`)
    REFERENCES `courses`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_enrollment_student` FOREIGN KEY (`user_id`)
    REFERENCES `student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Student enrollment and progress tracking per course';


-- =============================================================
--  4. LECTURE PROGRESS TABLE
--  Referenced by: LectureDAO.java (saveLectureProgress,
--  getLectureWithProgress, recalculateCourseProgress)
-- =============================================================
CREATE TABLE IF NOT EXISTS `lecture_progress` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `lecture_id` INT NOT NULL,
  `progress_seconds` INT NOT NULL DEFAULT 0,
  `completed` TINYINT(1) NOT NULL DEFAULT 0,
  `last_watched_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_progress_user_lecture` (`user_id`, `lecture_id`),
  INDEX `idx_progress_user` (`user_id`),
  INDEX `idx_progress_lecture` (`lecture_id`),
  CONSTRAINT `fk_progress_lecture` FOREIGN KEY (`lecture_id`)
    REFERENCES `lectures`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_progress_student` FOREIGN KEY (`user_id`)
    REFERENCES `student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Per-student watch progress per lecture (UPSERT via ON DUPLICATE KEY)';


-- =============================================================
--  5. VIDEO ACCESS LOGS TABLE
--  Referenced by: VideoAccessLogDAO.java
--  Audit and rate limiting log for DRM video requests
-- =============================================================
CREATE TABLE IF NOT EXISTS `video_access_logs` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `lecture_id` INT NOT NULL,
  `ip_address` VARCHAR(45) DEFAULT NULL,
  `user_agent` VARCHAR(500) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_val_ip_time` (`ip_address`, `created_at`),
  INDEX `idx_val_user` (`user_id`),
  INDEX `idx_val_lecture` (`lecture_id`),
  CONSTRAINT `fk_val_student` FOREIGN KEY (`user_id`)
    REFERENCES `student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_val_lecture` FOREIGN KEY (`lecture_id`)
    REFERENCES `lectures`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Audit log and rate limiter for video stream token generation';


-- =============================================================
--  6. ALTER `assignment` TABLE — Add missing columns
--  Referenced by: AssignmentDAO.java
--
--  Existing columns: assign_id, subject, assign_name, create_date
--  Adding: assignment_code, description, due_date,
--          instructions_text, total_marks, is_active,
--          instruction_pdf_path
-- =============================================================

-- Add assignment_code column (unique code per assignment)
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'assignment'
    AND COLUMN_NAME = 'assignment_code');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `assignment` ADD COLUMN `assignment_code` VARCHAR(50) DEFAULT NULL AFTER `assign_name`',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add description column
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'assignment'
    AND COLUMN_NAME = 'description');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `assignment` ADD COLUMN `description` TEXT DEFAULT NULL AFTER `assignment_code`',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add due_date column
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'assignment'
    AND COLUMN_NAME = 'due_date');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `assignment` ADD COLUMN `due_date` DATE DEFAULT NULL AFTER `description`',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add instructions_text column
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'assignment'
    AND COLUMN_NAME = 'instructions_text');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `assignment` ADD COLUMN `instructions_text` TEXT DEFAULT NULL AFTER `due_date`',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add total_marks column
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'assignment'
    AND COLUMN_NAME = 'total_marks');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `assignment` ADD COLUMN `total_marks` INT NOT NULL DEFAULT 100 AFTER `instructions_text`',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add is_active column
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'assignment'
    AND COLUMN_NAME = 'is_active');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `assignment` ADD COLUMN `is_active` TINYINT(1) NOT NULL DEFAULT 1 AFTER `total_marks`',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add instruction_pdf_path column
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'assignment'
    AND COLUMN_NAME = 'instruction_pdf_path');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `assignment` ADD COLUMN `instruction_pdf_path` VARCHAR(500) DEFAULT NULL AFTER `is_active`',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- =============================================================
--  7. ALTER `student_assignment_data` TABLE — Add assignment_master_id FK
--  Referenced by: AssignmentDAO.java (JOIN on assignment_master_id)
-- =============================================================

-- Add assignment_master_id column (FK linking to assignment.assign_id)
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'student_assignment_data'
    AND COLUMN_NAME = 'assignment_master_id');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `student_assignment_data` ADD COLUMN `assignment_master_id` INT DEFAULT NULL AFTER `assign_id`',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add index on assignment_master_id
SET @idx_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'student_assignment_data'
    AND INDEX_NAME = 'idx_sad_master_id');
SET @sql = IF(@idx_exists = 0,
  'ALTER TABLE `student_assignment_data` ADD INDEX `idx_sad_master_id` (`assignment_master_id`)',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add FK constraint linking assignment_master_id → assignment.assign_id
SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'student_assignment_data'
    AND CONSTRAINT_NAME = 'fk_sad_assignment_master');
SET @sql = IF(@fk_exists = 0,
  'ALTER TABLE `student_assignment_data` ADD CONSTRAINT `fk_sad_assignment_master` FOREIGN KEY (`assignment_master_id`) REFERENCES `assignment`(`assign_id`) ON DELETE SET NULL ON UPDATE CASCADE',
  'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- =============================================================
--  8. SAMPLE SEED DATA: Courses & Initial Lectures
-- =============================================================
INSERT INTO `courses` (`id`, `title`, `slug`, `description`, `thumbnail_url`, `instructor_name`, `category`, `difficulty`, `duration_weeks`, `total_lessons`, `status`)
SELECT 1, 'Java Web Development Masterclass', 'java-web-development-masterclass', 'Complete guide to building web applications with Java EE, Servlets, JSP, and MVC architecture. Learn from scratch to deployment.', NULL, 'Prof. Rahul Sharma', 'Engineering', 'Beginner', 8, 3, 'PUBLISHED'
WHERE NOT EXISTS (SELECT 1 FROM `courses` WHERE `id` = 1);

INSERT INTO `courses` (`id`, `title`, `slug`, `description`, `thumbnail_url`, `instructor_name`, `category`, `difficulty`, `duration_weeks`, `total_lessons`, `status`)
SELECT 2, 'Database Design & SQL Mastery', 'database-design-sql-mastery', 'Master relational database design, MySQL queries, optimization, and JDBC integration for Java applications.', NULL, 'Prof. Priya Mehta', 'Engineering', 'Intermediate', 6, 0, 'PUBLISHED'
WHERE NOT EXISTS (SELECT 2 FROM `courses` WHERE `id` = 2);

INSERT INTO `courses` (`id`, `title`, `slug`, `description`, `thumbnail_url`, `instructor_name`, `category`, `difficulty`, `duration_weeks`, `total_lessons`, `status`)
SELECT 3, 'Full Stack Project Development', 'full-stack-project-development', 'Build a complete full-stack application from requirements to deployment. Covers frontend, backend, database, and DevOps.', NULL, 'Prof. Rahul Sharma', 'Engineering', 'Advanced', 10, 0, 'DRAFT'
WHERE NOT EXISTS (SELECT 3 FROM `courses` WHERE `id` = 3);

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================
--  Migration V005 complete.
--  New tables: courses, lectures, enrollments,
--              lecture_progress, video_access_logs
--  Altered tables: assignment (7 new columns),
--                  student_assignment_data (assignment_master_id)
--  All user foreign keys reference production `student`(`id`).
-- =============================================================

