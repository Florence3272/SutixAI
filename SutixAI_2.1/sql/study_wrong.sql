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

 Date: 20/07/2026 16:25:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for study_wrong
-- ----------------------------
DROP TABLE IF EXISTS `study_wrong`;
CREATE TABLE `study_wrong`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `term_id` bigint NOT NULL COMMENT '术语ID',
  `wrong_count` int NOT NULL DEFAULT 1 COMMENT '答错次数',
  `last_wrong_time` datetime NULL DEFAULT NULL COMMENT '最后答错时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_term`(`user_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '错题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_wrong
-- ----------------------------
INSERT INTO `study_wrong` VALUES (1, 4, 2, 1, '2026-07-06 16:11:50', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
