DROP DATABASE IF EXISTS `sensitive-service`;
CREATE DATABASE IF NOT EXISTS `sensitive-service`;
USE `sensitive-service`;

DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `word` char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;
