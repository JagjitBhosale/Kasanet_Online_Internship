-- =============================================================
--  Student Internship Portal — Database Schema
--  Database  : online_internship
--  Charset   : utf8mb4
--  Engine    : InnoDB
--  Target    : MySQL 5.6 / 5.7  (MySQL Workbench 6.3 compatible)
--  Generated : 2026-10-04
-- =============================================================

-- ---------------------------------------------------------------
-- Bootstrap
-- ---------------------------------------------------------------
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Create and select the database
CREATE DATABASE IF NOT EXISTS `online_internship`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `online_internship`;

-- =============================================================
--  DROP existing tables (reverse FK order)
-- =============================================================
DROP TABLE IF EXISTS `login_activity`;
DROP TABLE IF EXISTS `lecture_progress`;
DROP TABLE IF EXISTS `lectures`;
DROP TABLE IF EXISTS `assignment_submissions`;
DROP TABLE IF EXISTS `assignments`;
DROP TABLE IF EXISTS `intercollege_competitions`;
DROP TABLE IF EXISTS `hackathons`;
DROP TABLE IF EXISTS `experience`;
DROP TABLE IF EXISTS `platform_project_reports`;
DROP TABLE IF EXISTS `platform_certificates`;
DROP TABLE IF EXISTS `projects`;
DROP TABLE IF EXISTS `certifications`;
DROP TABLE IF EXISTS `skills`;
DROP TABLE IF EXISTS `career_goals`;
DROP TABLE IF EXISTS `achievements`;
DROP TABLE IF EXISTS `engineering_year_marks`;
DROP TABLE IF EXISTS `education_details`;
DROP TABLE IF EXISTS `student_profiles`;
DROP TABLE IF EXISTS `users`;

-- =============================================================
--  TABLE: users
--  Core authentication / identity table for both Students & Admins.
-- =============================================================
CREATE TABLE IF NOT EXISTS `users` (
  `user_id`       INT            NOT NULL AUTO_INCREMENT,
  `first_name`    VARCHAR(50)    NOT NULL,
  `last_name`     VARCHAR(50)    NOT NULL,
  `email`         VARCHAR(100)   NOT NULL,
  `password_hash` VARCHAR(255)   NOT NULL COMMENT 'BCrypt hash — never store plain text',
  `mobile`        VARCHAR(15)    DEFAULT NULL,
  `country_code`  VARCHAR(5)     DEFAULT '+91',
  `role`          ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
  `stream`        ENUM('Graduation','Post Graduation','Diploma') DEFAULT NULL,
  `branch`        VARCHAR(100)   DEFAULT NULL,
  `college_name`  VARCHAR(150)   DEFAULT NULL,
  `city`          VARCHAR(100)   DEFAULT NULL,
  `referral_code` VARCHAR(20)    DEFAULT NULL,
  `is_active`     TINYINT(1)     NOT NULL DEFAULT 1,
  `created_at`    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uq_email`  (`email`),
  INDEX `idx_email`      (`email`),
  INDEX `idx_mobile`     (`mobile`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Core user / authentication table (students and admins)';

-- =============================================================
--  TABLE: student_profiles
--  Extended profile data linked 1-to-1 with users.
-- =============================================================
CREATE TABLE IF NOT EXISTS `student_profiles` (
  `profile_id`          INT           NOT NULL AUTO_INCREMENT,
  `user_id`             INT           NOT NULL,
  `gender`              ENUM('Male','Female','Other') DEFAULT NULL,
  `date_of_birth`       DATE          DEFAULT NULL,
  `address`             TEXT          DEFAULT NULL,
  `state`               VARCHAR(100)  DEFAULT NULL,
  `profile_photo_path`  VARCHAR(255)  DEFAULT NULL,
  `resume_path`         VARCHAR(255)  DEFAULT NULL,
  `resume_filename`     VARCHAR(255)  DEFAULT NULL,
  `resume_uploaded_at`  TIMESTAMP     NULL DEFAULT NULL,
  `profile_completion`  INT           NOT NULL DEFAULT 0 COMMENT 'Percentage 0-100',
  `last_login`          TIMESTAMP     NULL DEFAULT NULL,
  `previous_login`      TIMESTAMP     NULL DEFAULT NULL,
  `created_at`          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `uq_user_id` (`user_id`),
  CONSTRAINT `fk_sp_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Extended student profile (1-to-1 with users)';

-- =============================================================
--  TABLE: education_details
--  Academic history per user; one row per level.
-- =============================================================
CREATE TABLE IF NOT EXISTS `education_details` (
  `edu_id`              INT             NOT NULL AUTO_INCREMENT,
  `user_id`             INT             NOT NULL,
  `level`               ENUM('SSC','HSC','Graduation','Master','Diploma') NOT NULL,
  `school_college_name` VARCHAR(200)    DEFAULT NULL,
  `board_university`    VARCHAR(200)    DEFAULT NULL,
  `stream`              VARCHAR(100)    DEFAULT NULL,
  `passing_year`        YEAR            DEFAULT NULL,
  `percentage`          DECIMAL(5,2)    DEFAULT NULL,
  `created_at`          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`edu_id`),
  UNIQUE KEY `uq_user_level` (`user_id`, `level`),
  CONSTRAINT `fk_ed_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Academic history; one row per qualification level per user';

-- =============================================================
--  TABLE: engineering_year_marks
--  Optional engineering year-wise marks (First Year, Second Year, Third Year, Final Year)
--  Supports both Percentage and CGPA.
-- =============================================================
CREATE TABLE IF NOT EXISTS `engineering_year_marks` (
  `mark_id`       INT           NOT NULL AUTO_INCREMENT,
  `user_id`       INT           NOT NULL,
  `year_level`    VARCHAR(50)   NOT NULL COMMENT 'First Year, Second Year, Third Year, Final Year',
  `score_type`    ENUM('Percentage','CGPA') NOT NULL DEFAULT 'Percentage',
  `score_value`   DECIMAL(5,2)  NOT NULL,
  `passing_year`  YEAR          DEFAULT NULL,
  `college_name`  VARCHAR(200)  DEFAULT NULL,
  `created_at`    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`mark_id`),
  UNIQUE KEY `uq_user_year_level` (`user_id`, `year_level`),
  CONSTRAINT `fk_eym_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Engineering year-wise percentage/CGPA records';

-- =============================================================
--  TABLE: achievements
--  Certifications, projects, extra-curricular items (legacy combined).
-- =============================================================
CREATE TABLE IF NOT EXISTS `achievements` (
  `achievement_id`   INT          NOT NULL AUTO_INCREMENT,
  `user_id`          INT          NOT NULL,
  `achievement_type` ENUM('Certification','Project','ExtraCurricular','Other') NOT NULL,
  `title`            VARCHAR(200) NOT NULL,
  `description`      TEXT         DEFAULT NULL,
  `technology`       VARCHAR(200) DEFAULT NULL,
  `extra_activities` TEXT         DEFAULT NULL,
  `created_at`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`achievement_id`),
  INDEX `idx_ach_user` (`user_id`),
  CONSTRAINT `fk_ach_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='General achievements bucket (certifications, projects, etc.)';

-- =============================================================
--  TABLE: skills
--  Skill tags per user.
-- =============================================================
CREATE TABLE IF NOT EXISTS `skills` (
  `skill_id`    INT          NOT NULL AUTO_INCREMENT,
  `user_id`     INT          NOT NULL,
  `skill_name`  VARCHAR(100) NOT NULL,
  `skill_type`  VARCHAR(50)  NOT NULL,
  `proficiency` ENUM('Beginner','Intermediate','Advanced','Expert') DEFAULT NULL,
  `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`skill_id`),
  UNIQUE KEY `uq_user_skill` (`user_id`, `skill_name`, `skill_type`),
  INDEX `idx_skill_user` (`user_id`),
  CONSTRAINT `fk_sk_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Skills tagged to a user';

ALTER TABLE `skills`
  MODIFY COLUMN `proficiency` ENUM('Beginner','Intermediate','Advanced','Expert') DEFAULT NULL;

-- =============================================================
--  TABLE: certifications
--  Formal certifications with optional credential links.
-- =============================================================
CREATE TABLE IF NOT EXISTS `certifications` (
  `cert_id`        INT          NOT NULL AUTO_INCREMENT,
  `user_id`        INT          NOT NULL,
  `cert_name`      VARCHAR(200) NOT NULL,
  `provider`       VARCHAR(200) DEFAULT NULL,
  `technology`     VARCHAR(200) DEFAULT NULL,
  `cert_id_number` VARCHAR(100) DEFAULT NULL,
  `issue_date`     DATE         DEFAULT NULL,
  `expiry_date`    DATE         DEFAULT NULL,
  `credential_url` VARCHAR(500) DEFAULT NULL,
  `created_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cert_id`),
  INDEX `idx_cert_user` (`user_id`),
  CONSTRAINT `fk_ct_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Formal certifications earned by a student';

-- =============================================================
  --  TABLE: platform_certificates
  --  Exam results for certificates issued by the Kasnet platform.
  -- =============================================================
  CREATE TABLE IF NOT EXISTS `platform_certificates` (
    `certificate_id`  INT            NOT NULL AUTO_INCREMENT,
    `user_id`         INT            NOT NULL,
    `exam_name`       VARCHAR(200)   NOT NULL,
    `score_obtained`  DECIMAL(8,2)   NOT NULL,
    `total_score`     DECIMAL(8,2)   NOT NULL,
    `achievement_date` DATE          NOT NULL,
    `created_at`      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`certificate_id`),
    INDEX `idx_platform_cert_user` (`user_id`),
    CONSTRAINT `fk_platform_cert_user`
      FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
      ON DELETE CASCADE ON UPDATE CASCADE
  ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci
    COMMENT='Student exam results for certificates issued by the platform';

-- =============================================================
--  TABLE: platform_project_reports
--  Detailed project reports submitted through the student dashboard.
-- =============================================================
CREATE TABLE IF NOT EXISTS `platform_project_reports` (
    `report_id`                    INT            NOT NULL AUTO_INCREMENT,
    `user_id`                      INT            NOT NULL,
    `project_name`                 VARCHAR(200)   NOT NULL,
    `scope_details`                TEXT           NOT NULL,
    `project_domain`               VARCHAR(200)   DEFAULT NULL,
    `project_technology`           VARCHAR(500)   DEFAULT NULL,
    `project_application`          VARCHAR(500)   DEFAULT NULL,
    `project_duration`             VARCHAR(100)   DEFAULT NULL,
    `project_model`                VARCHAR(100)   NOT NULL,
    `requirement_gathering_time`   VARCHAR(100)   DEFAULT NULL,
    `project_analysis_time`        VARCHAR(100)   DEFAULT NULL,
    `project_designing_time`       VARCHAR(100)   DEFAULT NULL,
    `project_coding_time`          VARCHAR(100)   DEFAULT NULL,
    `project_testing_time`         VARCHAR(100)   DEFAULT NULL,
    `project_cost`                 DECIMAL(12,2)  DEFAULT NULL,
    `testing_type`                 VARCHAR(100)   DEFAULT NULL,
    `testing_duration`             VARCHAR(100)   DEFAULT NULL,
    `issues_found`                 INT            NOT NULL DEFAULT 0,
    `knowledge_requirement`        VARCHAR(40)    DEFAULT NULL,
    `knowledge_analysis`           VARCHAR(40)    DEFAULT NULL,
    `knowledge_designing`          VARCHAR(40)    DEFAULT NULL,
    `knowledge_coding`             VARCHAR(40)    DEFAULT NULL,
    `knowledge_testing`            VARCHAR(40)    DEFAULT NULL,
    `knowledge_post_implementation` VARCHAR(40)   DEFAULT NULL,
    `created_at`                   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`report_id`),
    INDEX `idx_platform_project_user` (`user_id`),
    CONSTRAINT `fk_platform_project_user`
      FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
      ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci
    COMMENT='Detailed project reports submitted by students through the platform';

    -- =============================================================
  --  TABLE: projects
--  Personal / academic / professional projects.
-- =============================================================
CREATE TABLE IF NOT EXISTS `projects` (
  `project_id`        INT          NOT NULL AUTO_INCREMENT,
  `user_id`           INT          NOT NULL,
  `project_name`      VARCHAR(200) NOT NULL,
  `description`       TEXT         DEFAULT NULL,
  `technologies_used` VARCHAR(500) DEFAULT NULL,
  `role`              VARCHAR(100) DEFAULT NULL,
  `project_type`      ENUM('Academic','Personal','Professional','Open Source') NOT NULL DEFAULT 'Academic',
  `team_size`         INT          NOT NULL DEFAULT 1,
  `start_date`        DATE         DEFAULT NULL,
  `end_date`          DATE         DEFAULT NULL,
  `project_url`       VARCHAR(500) DEFAULT NULL,
  `github_url`        VARCHAR(500) DEFAULT NULL,
  `created_at`        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`),
  INDEX `idx_proj_user` (`user_id`),
  CONSTRAINT `fk_pr_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Projects (academic, personal, professional, open-source) per user';

-- =============================================================
--  TABLE: experience
--  Professional work experience & internships (distinct from projects).
-- =============================================================
CREATE TABLE IF NOT EXISTS `experience` (
  `experience_id`      INT          NOT NULL AUTO_INCREMENT,
  `user_id`            INT          NOT NULL,
  `company_name`       VARCHAR(200) NOT NULL,
  `role`               VARCHAR(100) NOT NULL,
  `employment_type`    VARCHAR(50)  NOT NULL DEFAULT 'Full-time',
  `technologies_used`  VARCHAR(500) DEFAULT NULL,
  `description`        TEXT         DEFAULT NULL,
  `start_date`         DATE         DEFAULT NULL,
  `end_date`           DATE         DEFAULT NULL,
  `is_current`         TINYINT(1)   NOT NULL DEFAULT 0,
  `created_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`experience_id`),
  INDEX `idx_exp_user` (`user_id`),
  CONSTRAINT `fk_exp_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Professional work and internship experience per user';

-- =============================================================
--  TABLE: career_goals
--  Target career, role preferences, and domain direction per user.
-- =============================================================
CREATE TABLE IF NOT EXISTS `career_goals` (
  `goal_id`                INT          NOT NULL AUTO_INCREMENT,
  `user_id`                INT          NOT NULL,
  `target_role`            VARCHAR(100) NOT NULL,
  `target_role_other`      VARCHAR(100) DEFAULT NULL,
  `preferredDomain`        VARCHAR(100) DEFAULT NULL,
  `preferred_domain`       VARCHAR(100) DEFAULT NULL,
  `preferred_domain_other` VARCHAR(100) DEFAULT NULL,
  `career_objective`       TEXT         DEFAULT NULL,
  `created_at`             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`goal_id`),
  UNIQUE KEY `uq_cg_user` (`user_id`),
  INDEX `idx_cg_user` (`user_id`),
  CONSTRAINT `fk_cg_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Target career role and preferred domain for skill gap context';

-- =============================================================
--  TABLE: hackathons
--  Hackathon participation records.
-- =============================================================
CREATE TABLE IF NOT EXISTS `hackathons` (
  `hackathon_id`     INT          NOT NULL AUTO_INCREMENT,
  `user_id`          INT          NOT NULL,
  `hackathon_name`   VARCHAR(200) NOT NULL,
  `organizer`        VARCHAR(200) DEFAULT NULL,
  `event_date`       DATE         DEFAULT NULL,
  `position_result`  VARCHAR(100) DEFAULT NULL,
  `description`      TEXT         DEFAULT NULL,
  `team_name`        VARCHAR(100) DEFAULT NULL,
  `certificate_path` VARCHAR(255) DEFAULT NULL,
  `created_at`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`hackathon_id`),
  INDEX `idx_hack_user` (`user_id`),
  CONSTRAINT `fk_hk_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Hackathon participation history per user';

-- =============================================================
--  TABLE: intercollege_competitions
--  Inter-college / inter-school competition records.
-- =============================================================
CREATE TABLE IF NOT EXISTS `intercollege_competitions` (
  `competition_id`    INT          NOT NULL AUTO_INCREMENT,
  `user_id`           INT          NOT NULL,
  `competition_name`  VARCHAR(200) NOT NULL,
  `college_organizer` VARCHAR(200) DEFAULT NULL,
  `event_date`        DATE         DEFAULT NULL,
  `project_name`      VARCHAR(200) DEFAULT NULL,
  `position_result`   VARCHAR(100) DEFAULT NULL,
  `description`       TEXT         DEFAULT NULL,
  `team_name`         VARCHAR(100) DEFAULT NULL,
  `certificate_path`  VARCHAR(255) DEFAULT NULL,
  `created_at`        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`competition_id`),
  INDEX `idx_comp_user` (`user_id`),
  CONSTRAINT `fk_ic_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Inter-college competition participation records';

-- =============================================================
--  TABLE: assignments
--  Assignments created by admin/instructor (no user FK).
-- =============================================================
CREATE TABLE IF NOT EXISTS `assignments` (
  `assignment_id`     INT          NOT NULL AUTO_INCREMENT,
  `title`             VARCHAR(200) NOT NULL,
  `subject`           VARCHAR(200) DEFAULT NULL,
  `description`       TEXT         DEFAULT NULL,
  `instructions_text` TEXT         DEFAULT NULL,
  `instruction_pdf_path` VARCHAR(255) DEFAULT NULL,
  `assignment_number` VARCHAR(50)  DEFAULT NULL,
  `due_date`          DATE         DEFAULT NULL,
  `total_marks`       INT          NOT NULL DEFAULT 100,
  `is_active`         TINYINT(1)   NOT NULL DEFAULT 1,
  `created_at`        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`assignment_id`),
  UNIQUE KEY `uq_assignment_number` (`assignment_number`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Assignments published by instructors/admins';

-- =============================================================
--  TABLE: assignment_submissions
--  Student submissions for a given assignment.
-- =============================================================
CREATE TABLE IF NOT EXISTS `assignment_submissions` (
  `submission_id`   INT          NOT NULL AUTO_INCREMENT,
  `assignment_id`   INT          NOT NULL,
  `user_id`         INT          NOT NULL,
  `submission_date` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `file_path`       VARCHAR(255) DEFAULT NULL,
  `status`          ENUM('Pending','Submitted','Late','Evaluated') NOT NULL DEFAULT 'Pending',
  `marks_obtained`  INT          DEFAULT NULL,
  `remarks`         TEXT         DEFAULT NULL,
  `resubmitted`     TINYINT(1)   NOT NULL DEFAULT 0,
  `updated_at`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`submission_id`),
  INDEX `idx_sub_assignment` (`assignment_id`),
  INDEX `idx_sub_user`       (`user_id`),
  CONSTRAINT `fk_sub_assignment`
    FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`assignment_id`)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_sub_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Student submissions for published assignments';

-- =============================================================
--  TABLE: lectures
--  Video / lecture content published by admins.
-- =============================================================
CREATE TABLE IF NOT EXISTS `lectures` (
  `lecture_id`       INT          NOT NULL AUTO_INCREMENT,
  `title`            VARCHAR(200) NOT NULL,
  `subject`          VARCHAR(200) DEFAULT NULL,
  `description`      TEXT         DEFAULT NULL,
  `instructor`       VARCHAR(100) DEFAULT NULL,
  `duration_minutes` INT          DEFAULT NULL,
  `video_url`        VARCHAR(500) DEFAULT NULL,
  `lecture_number`   INT          DEFAULT NULL,
  `lecture_date`     DATE         DEFAULT NULL,
  `is_active`        TINYINT(1)   NOT NULL DEFAULT 1,
  `created_at`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`lecture_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Lecture / video content published for internship students';

-- =============================================================
--  TABLE: lecture_progress
--  Per-user viewing progress for each lecture.
-- =============================================================
CREATE TABLE IF NOT EXISTS `lecture_progress` (
  `progress_id`           INT        NOT NULL AUTO_INCREMENT,
  `lecture_id`            INT        NOT NULL,
  `user_id`               INT        NOT NULL,
  `is_completed`          TINYINT(1) NOT NULL DEFAULT 0,
  `watch_progress_percent` INT       NOT NULL DEFAULT 0 COMMENT '0-100',
  `last_watched`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`progress_id`),
  UNIQUE KEY `uq_lecture_user` (`lecture_id`, `user_id`),
  INDEX `idx_lp_user` (`user_id`),
  CONSTRAINT `fk_lp_lecture`
    FOREIGN KEY (`lecture_id`) REFERENCES `lectures` (`lecture_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_lp_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Tracks per-user viewing progress for each lecture';

-- =============================================================
--  TABLE: login_activity
--  Audit log of every successful login event.
-- =============================================================
CREATE TABLE IF NOT EXISTS `login_activity` (
  `activity_id` INT          NOT NULL AUTO_INCREMENT,
  `user_id`     INT          NOT NULL,
  `login_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address`  VARCHAR(50)  DEFAULT NULL,
  `user_agent`  VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`activity_id`),
  INDEX `idx_la_user`  (`user_id`),
  INDEX `idx_la_time`  (`login_time`),
  CONSTRAINT `fk_la_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Audit log of user login events';

-- =============================================================
--  SAMPLE DATA
-- =============================================================

-- -------------------------------------------------------------
--  Admin user
--  Credentials: admin@myinternshiptech.com / Admin@12345
--               admin@kasnet.in / Admin@12345 (legacy alias)
-- -------------------------------------------------------------
INSERT INTO `users`
  (`first_name`, `last_name`, `email`, `password_hash`, `mobile`, `country_code`, `role`, `is_active`)
VALUES
  ('MIT', 'Admin', 'admin@myinternshiptech.com',
   '$2a$12$AyM4Ut1sHpQo7jmZvv19L.fE5dxq0Tb6r.Cq42nWqEpf2EaiUtKZG',
   '9000000000', '+91', 'ADMIN', 1),
  ('MIT', 'Admin', 'admin@kasnet.in',
   '$2a$12$AyM4Ut1sHpQo7jmZvv19L.fE5dxq0Tb6r.Cq42nWqEpf2EaiUtKZG',
   '9000000000', '+91', 'ADMIN', 1);

-- -------------------------------------------------------------
--  Test student account
--  Credentials: test@myinternshiptech.com / Test@12345
-- -------------------------------------------------------------
INSERT INTO `users`
  (`first_name`, `last_name`, `email`, `password_hash`, `mobile`, `country_code`, `role`, `stream`, `branch`, `college_name`, `city`, `is_active`)
VALUES
  ('Test', 'Student', 'test@myinternshiptech.com',
   '$2a$12$wQfH0c0nSBXsVtZYJoKHzOeB80RhC6651LDqJxZZBJYudXBZSgZcO',
   '9000000001', '+91', 'STUDENT', 'Graduation', 'Computer Science and Engineering', 'Institute of Technology', 'Pune', 1);

INSERT INTO `student_profiles` (`user_id`)
SELECT `user_id` FROM `users` WHERE `email` = 'test@myinternshiptech.com';

-- -------------------------------------------------------------
--  Sample Assignments
-- -------------------------------------------------------------
INSERT INTO `assignments`
  (`title`, `subject`, `description`, `assignment_number`, `due_date`, `total_marks`, `is_active`)
VALUES
  (
    'Introduction to Java Servlets',
    'Java Web Development',
    'Create a basic servlet that handles GET and POST requests. Demonstrate request parameters, session management, and response writing. Submit a ZIP containing the project folder and a README.',
    'ASN-001',
    '2026-10-20',
    100,
    1
  ),
  (
    'Database Design & JDBC Integration',
    'Database Management',
    'Design a normalized MySQL database schema for a library management system with at least 4 tables. Implement CRUD operations using JDBC PreparedStatements. Document the ER diagram and submit source code.',
    'ASN-002',
    '2026-11-05',
    100,
    1
  );

-- -------------------------------------------------------------
--  Sample Lectures
-- -------------------------------------------------------------
INSERT INTO `lectures`
  (`title`, `subject`, `description`, `instructor`, `duration_minutes`, `video_url`, `lecture_number`, `lecture_date`, `is_active`)
VALUES
  (
    'Getting Started with Java EE & Tomcat Setup',
    'Java Web Development',
    'Overview of the Java EE ecosystem. Installing JDK 11+, Apache Tomcat 9, and configuring Eclipse/IntelliJ IDE for servlet development. Understanding the web.xml deployment descriptor.',
    'Prof. Rahul Sharma',
    45,
    'https://www.youtube.com/watch?v=example_lecture_1',
    1,
    '2026-10-06',
    1
  ),
  (
    'Servlet Lifecycle & HTTP Methods',
    'Java Web Development',
    'Deep dive into the Servlet lifecycle: init(), service(), and destroy(). Handling GET and POST methods. Reading request parameters, headers, and writing responses. Hands-on demo with form submission.',
    'Prof. Rahul Sharma',
    60,
    'https://www.youtube.com/watch?v=example_lecture_2',
    2,
    '2026-10-08',
    1
  ),
  (
    'JSP Fundamentals & MVC Architecture',
    'Java Web Development',
    'Introduction to JavaServer Pages (JSP). Scriptlets, EL expressions, and JSTL tags. Implementing the Model-View-Controller pattern with Servlets as controllers and JSP as views.',
    'Prof. Priya Mehta',
    55,
    'https://www.youtube.com/watch?v=example_lecture_3',
    3,
    '2026-10-10',
    1
  );

-- =============================================================
--  Re-enable foreign key checks
-- =============================================================
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================
--  END OF SCHEMA
-- =============================================================
