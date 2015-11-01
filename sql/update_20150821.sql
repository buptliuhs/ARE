ALTER TABLE `are`.`device` DROP INDEX `name_UNIQUE`;
ALTER TABLE `are`.`device` ADD COLUMN `user_id` INT NOT NULL DEFAULT 1 AFTER `id`;
ALTER TABLE `are`.`device` CHANGE COLUMN `user_id` `user_id` INT(11) NOT NULL;
