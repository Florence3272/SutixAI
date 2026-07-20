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

 Date: 20/07/2026 16:25:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for study_progress
-- ----------------------------
DROP TABLE IF EXISTS `study_progress`;
CREATE TABLE `study_progress`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `term_id` bigint NOT NULL COMMENT '术语ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'learning' COMMENT '学习状态',
  `review_count` int NOT NULL DEFAULT 0 COMMENT '复习次数',
  `last_review_time` datetime NULL DEFAULT NULL COMMENT '最后复习时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_term`(`user_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_term_id`(`term_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_progress
-- ----------------------------
INSERT INTO `study_progress` VALUES (1, 4, 1, 'mastered', 1, '2026-07-06 16:11:50', NULL, NULL);
INSERT INTO `study_progress` VALUES (2, 2, 4, 'mastered', 2, '2026-07-07 15:35:02', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
