-- ==============================================================================
-- Migration: V004__enhance_assignment_management.sql
-- Description: Adds assignment-management metadata and a typed student allocation
--              reference while retaining the legacy assignment identifiers.
-- Target: MySQL 5.7+ / 8.0+
-- Safety: Preserves existing tables and rows. No legacy assign_id values are
--         converted or backfilled. Existing assignments default to inactive.
-- ==============================================================================

-- The admin form accepts subjects up to 200 characters. Widening these columns
-- preserves all existing values and aligns the master and allocation records.
ALTER TABLE `assignment`
    MODIFY COLUMN `subject` VARCHAR(200) NOT NULL,
    ADD COLUMN `assignment_code` VARCHAR(50) DEFAULT NULL,
    ADD COLUMN `description` TEXT NULL,
    ADD COLUMN `due_date` DATE DEFAULT NULL,
    ADD COLUMN `instructions_text` TEXT NULL,
    ADD COLUMN `total_marks` INT(11) DEFAULT NULL,
    ADD COLUMN `is_active` TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN `instruction_pdf_path` VARCHAR(500) DEFAULT NULL,
    ADD UNIQUE KEY `uk_assignment_code` (`assignment_code`);

-- These legacy fields have no source in the assignment-admin form. Permit NULL
-- rather than fabricating an examination ID, company, level, or truncated
-- description. The new typed reference and master row are canonical.
ALTER TABLE `student_assignment_data`
    MODIFY COLUMN `examination_id` VARCHAR(100) DEFAULT NULL,
    MODIFY COLUMN `company_name` VARCHAR(255) DEFAULT NULL,
    MODIFY COLUMN `assign_subject` VARCHAR(200) NOT NULL,
    MODIFY COLUMN `assign_level` VARCHAR(350) DEFAULT NULL,
    MODIFY COLUMN `assign_descrition` VARCHAR(350) DEFAULT NULL,
    ADD COLUMN `assignment_master_id` INT(11) DEFAULT NULL AFTER `assign_id`,
    ADD UNIQUE KEY `uk_sad_assignment_student` (`assignment_master_id`, `student_id`),
    ADD CONSTRAINT `fk_sad_assignment_master`
        FOREIGN KEY (`assignment_master_id`) REFERENCES `assignment` (`assign_id`)
        ON DELETE RESTRICT
        ON UPDATE CASCADE;

-- ==============================================================================
-- Allocation and legacy-field mapping:
--   * assignment_master_id references assignment.assign_id (typed, authoritative).
--   * assign_id remains VARCHAR(255); for new rows, store the master assignment_code
--     verbatim as a legacy display/reference value. Never cast or backfill old values.
--   * assign_subject and assign_topic receive the master subject and assign_name.
--   * assign_descrition remains NULL; read the full description from the master row
--     rather than truncating it to this legacy VARCHAR(350) field.
--   * examination_id, company_name, and assign_level remain NULL because the
--     assignment workflow supplies no valid business values for them.
--   * assign_status is explicitly 'Assigned' for new allocations. Do not repurpose
--     assign_flag or assign_result. assign_code remains unused for submissions.
--   * create_date is the allocation date (CURRENT_DATE) for new rows.
--
-- Duplicate allocation protection:
--   uk_sad_assignment_student permits one non-NULL master/student pair. MySQL
--   permits multiple NULLs in a UNIQUE key, preserving unlinked legacy rows.
--   Existing allocation rows are intentionally left unlinked pending verified
--   mapping; duplicates among those rows cannot be detected by this new key.
--
-- Rollback instructions (destructive to data entered in the new fields):
--   Before rollback, back up the database and verify that no new metadata or
--   allocations need to be retained. The four newly nullable legacy fields must
--   contain no NULL values before restoring NOT NULL; the widened subject fields
--   must contain no value longer than 100 characters.
--
--   SELECT COUNT(*) AS null_legacy_fields
--   FROM `student_assignment_data`
--   WHERE `examination_id` IS NULL OR `company_name` IS NULL
--      OR `assign_level` IS NULL OR `assign_descrition` IS NULL;
--
--   SELECT COUNT(*) AS long_assignment_subjects
--   FROM `assignment` WHERE CHAR_LENGTH(`subject`) > 100;
--
--   SELECT COUNT(*) AS null_or_long_allocation_subjects
--   FROM `student_assignment_data`
--   WHERE `assign_subject` IS NULL OR CHAR_LENGTH(`assign_subject`) > 100;
--
--   If (and only if) the checks above return zero and new metadata has been
--   exported or is intentionally being discarded, run:
--   MySQL DDL implicitly commits; these rollback statements are not atomic.
--   Run them only after the checks and backup, and verify each step before
--   proceeding to the next.
--
--   ALTER TABLE `student_assignment_data`
--       DROP FOREIGN KEY `fk_sad_assignment_master`;
--
--   ALTER TABLE `student_assignment_data`
--       DROP INDEX `uk_sad_assignment_student`,
--       DROP COLUMN `assignment_master_id`,
--       MODIFY COLUMN `examination_id` VARCHAR(100) NOT NULL,
--       MODIFY COLUMN `company_name` VARCHAR(255) NOT NULL,
--       MODIFY COLUMN `assign_subject` VARCHAR(100) NOT NULL,
--       MODIFY COLUMN `assign_level` VARCHAR(350) NOT NULL,
--       MODIFY COLUMN `assign_descrition` VARCHAR(350) NOT NULL;
--
--   ALTER TABLE `assignment`
--       DROP INDEX `uk_assignment_code`,
--       DROP COLUMN `assignment_code`,
--       DROP COLUMN `description`,
--       DROP COLUMN `due_date`,
--       DROP COLUMN `instructions_text`,
--       DROP COLUMN `total_marks`,
--       DROP COLUMN `is_active`,
--       DROP COLUMN `instruction_pdf_path`,
--       MODIFY COLUMN `subject` VARCHAR(100) NOT NULL;
--
--   Rollback drops all values written to the added metadata columns. Relative PDF
--   paths will be lost from the database; files stored outside MySQL may remain
--   orphaned. New allocation rows using NULL legacy fields prevent restoration
--   of the original NOT NULL constraints without supplying verified business
--   values. In that case, restore from a pre-migration backup or perform a
--   separately reviewed data-preserving rollback; do not invent replacement data.
-- ==============================================================================
