-- ==============================================================================
-- Migration: V001__add_student_indexes.sql
-- Description: Adds non-unique lookup indexes on the production student table
-- Target: MySQL 5.7+ / 8.0+
-- Safety: Additive only. Preserves all existing data and constraints.
-- ==============================================================================

-- 1. Index on student email for rapid authentication and duplicate verification
-- Using prefix length 191 to guarantee compatibility across latin1, utf8, and utf8mb4 charsets
CREATE INDEX idx_student_email ON `student` (`email`(191));

-- 2. Index on student phone/mobile number for rapid phone lookup
CREATE INDEX idx_student_phone ON `student` (`phone`);

-- 3. Index on student referral code for referral tracking
CREATE INDEX idx_student_referalCode ON `student` (`referalCode`);

-- ==============================================================================
-- Rollback Instructions:
-- To safely revert this migration without affecting data:
-- ALTER TABLE `student` DROP INDEX `idx_student_email`;
-- ALTER TABLE `student` DROP INDEX `idx_student_phone`;
-- ALTER TABLE `student` DROP INDEX `idx_student_referalCode`;
-- ==============================================================================
