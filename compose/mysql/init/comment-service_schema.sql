DROP DATABASE IF EXISTS `comment-service`;
CREATE DATABASE IF NOT EXISTS `comment-service`;
USE `comment-service`;

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pid` bigint NOT NULL COMMENT '被回复的帖子ID',
  `from_uid` bigint NOT NULL COMMENT '回复者ID',
  `to_uid` bigint NULL DEFAULT NULL COMMENT '回复谁',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '回复的内容',
  `parent_id` int NULL DEFAULT NULL COMMENT '子级回复的父级回复ID，根级评论为0',
  `reply_id` int NULL DEFAULT NULL COMMENT '楼中楼中回复目标楼层的ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `comments_pid`(`pid` ASC) USING BTREE,
  INDEX `comments_uid`(`from_uid` ASC) USING BTREE
  -- 其他微服务
  -- ,CONSTRAINT `comments_pid` FOREIGN KEY (`pid`) REFERENCES `post` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  -- CONSTRAINT `comments_uid` FOREIGN KEY (`from_uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;