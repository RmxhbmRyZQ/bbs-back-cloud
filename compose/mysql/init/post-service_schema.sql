DROP DATABASE IF EXISTS `post-service`;
CREATE DATABASE IF NOT EXISTS `post-service`;
USE `post-service`;

DROP TABLE IF EXISTS `tag_option`;
CREATE TABLE `tag_option`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `label`(`label` ASC) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE,
  INDEX `id`(`id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `option_id` int NULL DEFAULT NULL COMMENT '标签类型ID',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签英文标识',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签显示名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签图标',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签描述',
  `creator` bigint NULL DEFAULT NULL COMMENT '标签创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '标签创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tag_label`(`label` ASC) USING BTREE,
  UNIQUE INDEX `tag_name`(`name` ASC) USING BTREE,
  INDEX `t_u_creator`(`creator` ASC) USING BTREE,
  INDEX `t_t_option_id`(`option_id` ASC) USING BTREE,
  CONSTRAINT `t_t_option_id` FOREIGN KEY (`option_id`) REFERENCES `tag_option` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
  -- ,CONSTRAINT `t_u_creator` FOREIGN KEY (`creator`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uid` bigint NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL COMMENT '帖子发布时间',
  `priority` double NULL DEFAULT 1 COMMENT '帖子权重, 值越大越靠前',
  `status` int NULL DEFAULT 0 COMMENT '0-正常; 1-精华; 2-拉黑;',
  `type` int NULL DEFAULT 0 COMMENT '0-正常; 1-置顶; 2-全局置顶;',
  `reply_number` int NULL DEFAULT 0 COMMENT '帖子被回复数',
  `view_number` int NULL DEFAULT 0 COMMENT '帖子被查看数',
  `last_comment_time` datetime NULL DEFAULT NULL COMMENT '帖子里最后一条评论的时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `post_uid`(`uid` ASC) USING BTREE
  -- ,CONSTRAINT `post_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1662616128926527490 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pid` bigint NOT NULL COMMENT '帖子ID',
  `tag_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签英文标识',
  `creator` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `tag_label`) USING BTREE,
  INDEX `p_t_pid`(`pid` ASC) USING BTREE,
  INDEX `p_t_tag_label`(`tag_label` ASC) USING BTREE,
  INDEX `p_t_creator`(`creator` ASC) USING BTREE,
  -- CONSTRAINT `p_t_creator` FOREIGN KEY (`creator`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `p_t_pid` FOREIGN KEY (`pid`) REFERENCES `post` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `p_t_tag_label` FOREIGN KEY (`tag_label`) REFERENCES `tag` (`label`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;