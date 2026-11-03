-- Adds storage for platform exam results that have no equivalent columns in
-- the production schema. Existing certificate PDFs remain in `certificate`.
CREATE TABLE IF NOT EXISTS `platform_certificates` (
    `certificate_id` INT(11) NOT NULL AUTO_INCREMENT,
    `user_id` INT(11) NOT NULL,
    `exam_name` VARCHAR(200) NOT NULL,
    `score_obtained` DECIMAL(7,2) NOT NULL,
    `total_score` DECIMAL(7,2) NOT NULL DEFAULT 1000.00,
    `achievement_date` DATE NOT NULL,
    PRIMARY KEY (`certificate_id`),
    KEY `idx_platform_certificates_user_date` (`user_id`, `achievement_date`),
    CONSTRAINT `fk_platform_certificates_student`
        FOREIGN KEY (`user_id`) REFERENCES `student` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
