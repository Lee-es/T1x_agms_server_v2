
# DELIMITER $$
# DROP PROCEDURE IF EXISTS deviceWhileInsert2$$
#
# CREATE PROCEDURE deviceWhileInsert2()
# BEGIN
#     DECLARE i INT DEFAULT 0;
#     DECLARE j INT DEFAULT 0;
#     DECLARE x INT DEFAULT 1;
#     DECLARE str VARCHAR(255);
#
#     WHILE (x <= 14) DO
#             SET i = 0;
#             WHILE i < 24  DO
#                     SET j = 0;
#                     WHILE j < 60 DO
#                             SET str = concat('2022-08-',x,' ',i,':',j);
#                             INSERT INTO spring_test.device(create_data_time, device_id , diabetes_level, user_user_idx, created_at, updated_at)
#                             VALUES(str, "device1", ROUND(RAND() * 100 + 50),1, now(), now());
#                             SET j = j + 1;
#                         END WHILE;
#                     SET i = i + 1;
#                 END WHILE;
#             SET x = x + 1;
#         END WHILE;
#
#
# END$$
# DELIMITER $$
#
#
# CALL deviceWhileInsert2();

# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (1,now(),now(),"2022-07-25 00:00:00", "device1", 50, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (2,now(),now(),"2022-07-25 00:02:00", "device1", 70, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (3,now(),now(),"2022-07-25 00:04:00", "device1", 180, 1);
# # ----- 1일차 -----
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (4,now(),now(),"2022-07-26 00:00:00", "device1", 51, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (5,now(),now(),"2022-07-26 00:02:00", "device1", 54, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (6,now(),now(),"2022-07-26 00:04:00", "device1", 105, 1);
# # ----- 2일차 -----
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (7,now(),now(),"2022-07-27 00:00:00", "device1", 102, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (8,now(),now(),"2022-07-27 00:02:00", "device1", 100, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (9,now(),now(),"2022-07-27 00:04:00", "device1", 72, 1);
# # ------ 3일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (10,now(),now(),"2022-07-28 00:00:00", "device1", 250, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (11,now(),now(),"2022-07-28 00:02:00", "device1", 251, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (12,now(),now(),"2022-07-28 00:04:00", "device1", 255, 1);
# # ------ 4일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (13,now(),now(),"2022-07-29 00:00:00", "device1", 53, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (14,now(),now(),"2022-07-29 00:02:00", "device1", 52, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (15,now(),now(),"2022-07-29 00:04:00", "device1", 95, 1);
# # ------ 5일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (16,now(),now(),"2022-07-30 00:00:00", "device1", 98, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (17,now(),now(),"2022-07-30 00:02:00", "device1", 181, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (18,now(),now(),"2022-07-30 00:04:00", "device1", 182, 1);
# # ------ 6일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (19,now(),now(),"2022-07-31 00:00:00", "device1", 180, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (20,now(),now(),"2022-07-31 00:02:00", "device1", 65, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (21,now(),now(),"2022-07-31 00:04:00", "device1", 255, 1);
# # ------ 7일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (22,now(),now(),"2022-08-01 00:00:00", "device1", 251, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (23,now(),now(),"2022-08-01 00:02:00", "device1", 70, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (24,now(),now(),"2022-08-01 00:04:00", "device1", 101, 1);
# # ------ 8일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (25,now(),now(),"2022-08-02 00:00:00", "device1", 100, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (26,now(),now(),"2022-08-02 00:02:00", "device1", 100, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (27,now(),now(),"2022-08-02 00:04:00", "device1", 90, 1);
# # ------ 9일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (28,now(),now(),"2022-08-03 00:00:00", "device1", 101, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (29,now(),now(),"2022-08-03 00:02:00", "device1", 102, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (30,now(),now(),"2022-08-03 00:04:00", "device1", 105, 1);
# # ------ 10일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (31,now(),now(),"2022-08-04 00:00:00", "device1", 98, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (32,now(),now(),"2022-08-04 00:02:00", "device1", 94, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (33,now(),now(),"2022-08-04 00:04:00", "device1", 95, 1);
# # ------ 11일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (34,now(),now(),"2022-08-05 00:00:00", "device1", 98, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (35,now(),now(),"2022-08-05 00:02:00", "device1", 102, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (36,now(),now(),"2022-08-05 00:04:00", "device1", 105, 1);
# # ------ 12일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (37,now(),now(),"2022-08-06 00:00:00", "device1", 95, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (38,now(),now(),"2022-08-06 00:02:00", "device1", 96, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (39,now(),now(),"2022-08-06 00:04:00", "device1", 95, 1);
# # ------ 13일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (40,now(),now(),"2022-08-07 00:00:00", "device1", 99, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (41,now(),now(),"2022-08-07 00:02:00", "device1", 101, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (42,now(),now(),"2022-08-07 00:04:00", "device1", 105, 1);
# # ------ 14일차 ------
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (43,now(),now(),"2022-08-07 01:00:00", "device1", 120, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (44,now(),now(),"2022-08-08 01:00:00", "device1", 70, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (45,now(),now(),"2022-08-09 01:00:00", "device1", 95, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (46,now(),now(),"2022-08-07 02:00:00", "device1", 170, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (47,now(),now(),"2022-08-08 02:00:00", "device1", 80, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (48,now(),now(),"2022-08-09 02:00:00", "device1", 90, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (49,now(),now(),"2022-08-07 03:00:00", "device1", 80, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (50,now(),now(),"2022-08-08 03:00:00", "device1", 70, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (51,now(),now(),"2022-08-09 03:00:00", "device1", 105, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (52,now(),now(),"2022-08-07 04:00:00", "device1", 120, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (53,now(),now(),"2022-08-08 04:00:00", "device1", 110, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (54,now(),now(),"2022-08-09 04:00:00", "device1", 105, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (55,now(),now(),"2022-08-07 05:00:00", "device1", 100, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (56,now(),now(),"2022-08-08 05:00:00", "device1", 95, 1);
#
# insert into spring_test.device (idx, created_at, updated_at, create_data_time, device_id, diabetes_level, user_user_idx)
# values (57,now(),now(),"2022-08-09 05:00:00", "device1", 105, 1);