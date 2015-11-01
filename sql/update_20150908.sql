ALTER TABLE `are`.`project` 
ADD INDEX `user_project_idx` (`user_id` ASC);
ALTER TABLE `are`.`project` 
ADD CONSTRAINT `user_project`
  FOREIGN KEY (`user_id`)
  REFERENCES `are`.`sys_user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `are`.`subject` 
ADD CONSTRAINT `project_subject`
  FOREIGN KEY (`project_id`)
  REFERENCES `are`.`project` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `are`.`data` 
ADD INDEX `subject_data_idx` (`subject_id` ASC);
ALTER TABLE `are`.`data` 
ADD CONSTRAINT `subject_data`
  FOREIGN KEY (`subject_id`)
  REFERENCES `are`.`subject` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `are`.`data` 
ADD CONSTRAINT `project_data`
  FOREIGN KEY (`project_id`)
  REFERENCES `are`.`project` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `are`.`subject` 
ADD CONSTRAINT `device_subject`
  FOREIGN KEY (`device_id`)
  REFERENCES `are`.`device` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `are`.`device` 
CHANGE COLUMN `type` `type` INT NOT NULL ;

ALTER TABLE `are`.`device` 
ADD INDEX `type_device_idx` (`type` ASC);
ALTER TABLE `are`.`device` 
ADD CONSTRAINT `type_device`
  FOREIGN KEY (`type`)
  REFERENCES `are`.`sys_device_type` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `are`.`sys_setting` 
ADD INDEX `user_setting_idx` (`user_id` ASC);
ALTER TABLE `are`.`sys_setting` 
ADD CONSTRAINT `user_setting`
  FOREIGN KEY (`user_id`)
  REFERENCES `are`.`sys_user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `are`.`sys_user` 
ADD INDEX `role_user_idx` (`role` ASC);
ALTER TABLE `are`.`sys_user` 
ADD CONSTRAINT `role_user`
  FOREIGN KEY (`role`)
  REFERENCES `are`.`sys_role` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `are`.`device` 
ADD INDEX `user_device_idx` (`user_id` ASC);
ALTER TABLE `are`.`device` 
ADD CONSTRAINT `user_device`
  FOREIGN KEY (`user_id`)
  REFERENCES `are`.`sys_user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
