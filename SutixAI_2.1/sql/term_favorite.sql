/*
 Navicat Premium Dump SQL

 Source Server         : shs
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : sutix_term_platform

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 20/07/2026 16:26:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for term_favorite
-- ----------------------------
DROP TABLE IF EXISTS `term_favorite`;
CREATE TABLE `term_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_term`(`user_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `term_id`(`term_id` ASC) USING BTREE,
  CONSTRAINT `term_favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `term_favorite_ibfk_2` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of term_favorite
-- ----------------------------
INSERT INTO `term_favorite` VALUES (33, 2, 4, NULL);
INSERT INTO `term_favorite` VALUES (34, 2, 3, NULL);

SET FOREIGN_KEY_CHECKS = 1;
