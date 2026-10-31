-- Add demo credentials to an existing online_internship database.
-- This script does not drop tables. It resets credentials only for these demo emails.
USE `online_internship`;

INSERT INTO `users`
  (`first_name`, `last_name`, `email`, `password_hash`, `mobile`, `country_code`, `role`, `is_active`)
VALUES ('MIT', 'Admin', 'admin@myinternshiptech.com',
        '$2a$12$AyM4Ut1sHpQo7jmZvv19L.fE5dxq0Tb6r.Cq42nWqEpf2EaiUtKZG',
        '9000000000', '+91', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE
  `password_hash` = VALUES(`password_hash`),
  `role` = VALUES(`role`),
  `is_active` = VALUES(`is_active`);

INSERT INTO `users`
  (`first_name`, `last_name`, `email`, `password_hash`, `mobile`, `country_code`, `role`, `is_active`)
VALUES ('MIT', 'Admin', 'admin@kasnet.in',
        '$2a$12$AyM4Ut1sHpQo7jmZvv19L.fE5dxq0Tb6r.Cq42nWqEpf2EaiUtKZG',
        '9000000000', '+91', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE
  `password_hash` = VALUES(`password_hash`),
  `role` = VALUES(`role`),
  `is_active` = VALUES(`is_active`);

INSERT INTO `users`
  (`first_name`, `last_name`, `email`, `password_hash`, `mobile`, `country_code`, `role`, `stream`, `branch`, `college_name`, `city`, `is_active`)
VALUES ('Test', 'Student', 'test@myinternshiptech.com',
        '$2a$12$wQfH0c0nSBXsVtZYJoKHzOeB80RhC6651LDqJxZZBJYudXBZSgZcO',
        '9000000001', '+91', 'STUDENT', 'Graduation', 'Computer Science and Engineering', 'Institute of Technology', 'Pune', 1)
ON DUPLICATE KEY UPDATE
  `password_hash` = VALUES(`password_hash`),
  `role` = VALUES(`role`),
  `is_active` = VALUES(`is_active`);

INSERT INTO `student_profiles` (`user_id`)
SELECT `u`.`user_id`
FROM `users` `u`
LEFT JOIN `student_profiles` `p` ON `p`.`user_id` = `u`.`user_id`
WHERE `u`.`email` = 'test@myinternshiptech.com'
  AND `p`.`user_id` IS NULL;