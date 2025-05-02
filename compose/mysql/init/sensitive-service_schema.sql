DROP DATABASE IF EXISTS `sensitive-service`;
CREATE DATABASE IF NOT EXISTS `sensitive-service`;
USE `sensitive-service`;

DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `word` char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

INSERT INTO `sensitive_word` VALUES (1, '政治');
INSERT INTO `sensitive_word` VALUES (2, '意识形态');
INSERT INTO `sensitive_word` VALUES (3, '疫情');
INSERT INTO `sensitive_word` VALUES (4, '中共');
INSERT INTO `sensitive_word` VALUES (5, '共产党');
INSERT INTO `sensitive_word` VALUES (6, '中国共产党');
INSERT INTO `sensitive_word` VALUES (7, '国民党');
INSERT INTO `sensitive_word` VALUES (8, '民进党');
INSERT INTO `sensitive_word` VALUES (9, '共产主义');
INSERT INTO `sensitive_word` VALUES (10, '政治');
