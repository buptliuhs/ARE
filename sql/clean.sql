user_id = xxx;

delete from sys_user where id = <<user_id>>;

delete from project where user_id not in (select id from sys_user);

delete from subject where project_id not in (select id from project);

delete from data where subject_id not in (select id from subject);

delete from device where user_id not in (select id from sys_user);

delete from task where k not in (select id from sys_user);

delete from sys_setting where user_id not in (select id from sys_user);

