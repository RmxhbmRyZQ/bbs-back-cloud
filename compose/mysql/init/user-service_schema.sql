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