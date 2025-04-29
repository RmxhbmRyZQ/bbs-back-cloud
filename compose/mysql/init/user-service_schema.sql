DROP DATABASE IF EXISTS `user-service`;
CREATE DATABASE IF NOT EXISTS `user-service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `user-service`;

DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority`  (
  `aid` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `authorities` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限范围',
  PRIMARY KEY (`aid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户邮箱',
  `email_verified` tinyint NULL DEFAULT 0 COMMENT '邮箱是否激活 - { 0:未激活, 1:已激活 }',
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐值',
  `register_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册IP',
  `create_time` datetime NULL DEFAULT NULL COMMENT '账号注册时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '账号资料更新时间',
  `updated_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被谁更新账户',
  `banned` tinyint NULL DEFAULT 0 COMMENT '账号状态， - { 0:未禁用, 1:已禁用 }',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '账号是否删除 - { 0:未删除, 1:已删除 }',
  PRIMARY KEY (`uid`) USING BTREE,
  UNIQUE KEY `uk_uid` (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  INDEX `uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `banned`;
CREATE TABLE `banned`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL,
  `deadline` datetime NULL DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `b_u_uid`(`uid` ASC) USING BTREE,
  CONSTRAINT `b_u_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `banned_history`;
CREATE TABLE `banned_history`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `deadline` datetime NULL DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `uid`) USING BTREE,
  INDEX `bh_u_uid`(`uid` ASC) USING BTREE,
  CONSTRAINT `bh_u_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `rid` int NOT NULL COMMENT '角色ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色英文名称',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色别名',
  `index` int NULL DEFAULT NULL COMMENT '角色排序号，越小越靠前',
  PRIMARY KEY (`rid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `role_authority`;
CREATE TABLE `role_authority`  (
  `rid` int NULL DEFAULT NULL COMMENT '角色ID',
  `aid` int NULL DEFAULT NULL COMMENT '权限ID',
  INDEX `r_a_rid`(`rid` ASC) USING BTREE,
  INDEX `r_a_aid`(`aid` ASC) USING BTREE,
  CONSTRAINT `r_a_aid` FOREIGN KEY (`aid`) REFERENCES `authority` (`aid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `r_a_rid` FOREIGN KEY (`rid`) REFERENCES `role` (`rid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `user_authority`;
CREATE TABLE `user_authority`  (
  `uid` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `aid` int NULL DEFAULT NULL COMMENT '权限ID',
  INDEX `u_a_uid`(`uid` ASC) USING BTREE,
  INDEX `u_a_aid`(`aid` ASC) USING BTREE,
  CONSTRAINT `u_a_aid` FOREIGN KEY (`aid`) REFERENCES `authority` (`aid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `u_a_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `uid` bigint NOT NULL,
  `rid` int NOT NULL,
  INDEX `ur_rid`(`rid` ASC) USING BTREE,
  INDEX `ur_uid`(`uid` ASC) USING BTREE,
  CONSTRAINT `u_r_rid` FOREIGN KEY (`rid`) REFERENCES `role` (`rid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `u_r_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

INSERT INTO `authority` VALUES (1, '编辑用户', 'manage:user:edit');
INSERT INTO `authority` VALUES (2, '封禁用户', 'manage:user:ban');
INSERT INTO `authority` VALUES (3, '发布帖子', 'activity:post:write');
INSERT INTO `authority` VALUES (4, '编辑帖子', 'activity:post:edit');
INSERT INTO `authority` VALUES (5, '删除帖子', 'activity:post:delete');
INSERT INTO `authority` VALUES (6, '发布评论', 'activity:comment:write');
INSERT INTO `authority` VALUES (7, '编辑评论', 'activity:comment:edit');
INSERT INTO `authority` VALUES (8, '删除评论', 'activity:comment:delete');
INSERT INTO `authority` VALUES (9, '发布回复', 'activity:reply:write');
INSERT INTO `authority` VALUES (10, '编辑回复', 'activity:reply:edit');
INSERT INTO `authority` VALUES (11, '删除回复', 'activity:reply:delete');
INSERT INTO `authority` VALUES (12, '更改标题', 'activity:title:edit');

INSERT INTO `role` VALUES (1, 'ADMIN', '管理员', 1);
INSERT INTO `role` VALUES (2, 'MODERATOR', '审查员', 2);
INSERT INTO `role` VALUES (3, 'USER', '普通用户', 3);
INSERT INTO `role` VALUES (4, 'UNVERIFIED', '未认证用户', 4);

INSERT INTO `role_authority` VALUES (2, 1);
INSERT INTO `role_authority` VALUES (2, 2);
INSERT INTO `role_authority` VALUES (2, 3);
INSERT INTO `role_authority` VALUES (2, 4);
INSERT INTO `role_authority` VALUES (2, 5);
INSERT INTO `role_authority` VALUES (2, 6);
INSERT INTO `role_authority` VALUES (2, 7);
INSERT INTO `role_authority` VALUES (2, 8);
INSERT INTO `role_authority` VALUES (2, 9);
INSERT INTO `role_authority` VALUES (2, 10);
INSERT INTO `role_authority` VALUES (2, 11);
INSERT INTO `role_authority` VALUES (3, 9);
INSERT INTO `role_authority` VALUES (3, 3);
INSERT INTO `role_authority` VALUES (3, 6);
INSERT INTO `role_authority` VALUES (2, 12);

INSERT INTO `user` VALUES (1, 1591314015905873922, 'admin', 'admin', '$2a$10$.gLOkB899/RV9RaIp18MZOj9.v4RTec6d2TvETkhe/MmC/TZqxmQS', 'http://localhost:8080/avatar/1591314015905873922/1710045410536.png', '0@1.1', 1, 'ca173', '127.0.0.1', '2022-11-12 14:14:21', '2023-02-24 14:55:51', NULL, 0, 0);
INSERT INTO `user` VALUES (2, 1591314523924168706, 'moderator', '测试用户', '$2a$10$.gLOkB899/RV9RaIp18MZOj9.v4RTec6d2TvETkhe/MmC/TZqxmQS', 'http://localhost:8080/avatar/1591314015905873923/1710045410536.png', '1@1.1', 0, '8f5be', '127.0.0.1', '2022-11-12 14:14:21', '2022-11-24 20:36:51', NULL, 0, 0);
INSERT INTO `user` VALUES (3, 1591315551016943618, 'user', 'user', '$2a$10$/S/MjNIGh9vNOowcCDDZyerv4jCf7CnlrHSH6edHdrHAHsO78YqEa', 'http://localhost:8080/avatar/1591314015905873924/1710045410536.png', '1@1.2', 1, 'a1e7a', '127.0.0.1', '2022-11-12 14:14:21', NULL, NULL, 0, 0);
INSERT INTO `user` VALUES (4, 1591317256513216513, 'user1', 'user1', '$2a$10$WxTbGtZq7dzFyOZCw..gWekS8anro5HVIJ3qixRN7Pzq59sXbXXHi', 'http://localhost:8080/avatar/1591314015905873925/1710045410536.png', '1@1.3', 0, '78116', '127.0.0.1', '2022-11-12 14:29:33', NULL, NULL, 0, 0);
INSERT INTO `user` VALUES (5, 1591317667206881281, 'user2', 'user2', '$2a$10$id.z3XBlUp6.fHUprznI9Oni49d.8E38On8hc7pVMfhxcgZxsA5O.', 'http://localhost:8080/avatar/1591314015905873926/1710045410536.png', '1@1.4', 1, 'bd028', '127.0.0.1', '2022-11-12 14:31:11', '2023-04-21 10:24:15', NULL, 0, 0);
INSERT INTO `user` VALUES (6, 1591318333363019777, 'com1', 'com1', '$2a$10$z4deAymc4yoFcAAKtlj74er8k4gmjlQU4dkGkL3eE2WSBmWWW7gOq', 'http://localhost:8080/avatar/1591314015905873927/1710045410536.png', '1@1.5', 0, 'b6bcf', '127.0.0.1', '2022-11-12 14:33:50', NULL, NULL, 0, 0);
INSERT INTO `user` VALUES (7, 1593245065385066497, 'com2', 'com2', '$2a$10$Ciqs5Qew0zw/iA35Bm2pquRkHvlrMm8vh826dpCJqWKGKxyRjDu0K', 'http://localhost:8080/avatar/1591314015905873928/1710045410536.png', '1@1.6', 0, 'd2871', '127.0.0.1', '2022-11-17 22:09:59', NULL, NULL, 0, 0);
INSERT INTO `user` VALUES (8, 1596342424588832769, 'com3', 'com3', '$2a$10$EIOj3rBBHgiOjzNE2lGHZ.13bvwzgeTfZVgogpzHge8DIoHYVC6Pa', 'http://localhost:8080/avatar/1591314015905873929/1710045410536.png', '1@1.7', 0, 'a51c7', '127.0.0.1', '2022-11-26 11:17:47', '2023-05-27 10:33:06', NULL, 0, 0);
INSERT INTO `user` VALUES (9, 1652656040866598913, 'user3', 'user3', '$2a$10$fo7uEd8tiyjtLvdGtPybJuDaAGGKNSYHEqHIIGRIh85aoPhJ/T8ie', 'http://localhost:8080/avatar/1591314015905873923/1710045410536.png', '1@1.8', 0, '06176', '127.0.0.1', '2023-04-30 20:47:59', '2023-05-12 15:16:45', NULL, 0, 0);

INSERT INTO `user_role` VALUES (1591314015905873922, 1);
INSERT INTO `user_role` VALUES (1591314523924168706, 2);
INSERT INTO `user_role` VALUES (1591315551016943618, 3);
INSERT INTO `user_role` VALUES (1591317256513216513, 4);
INSERT INTO `user_role` VALUES (1591317667206881281, 3);
INSERT INTO `user_role` VALUES (1591318333363019777, 4);
INSERT INTO `user_role` VALUES (1593245065385066497, 4);
INSERT INTO `user_role` VALUES (1596342424588832769, 4);
INSERT INTO `user_role` VALUES (1652656040866598913, 4);
