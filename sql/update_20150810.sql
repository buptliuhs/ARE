update sys_setting set name = 'sma_h' where name = 'static_dynamic_TH_H';
update sys_setting set name = 'sma_l' where name = 'static_dynamic_TH_L';
delete from sys_setting where name = 'F_CUT_OFF_HIGHER';
delete from sys_setting where name = 'ANGULAR_VEL_ROTATION';
