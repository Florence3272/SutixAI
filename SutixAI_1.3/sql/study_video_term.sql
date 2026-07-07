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

 Date: 07/07/2026 14:17:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for study_video_term
-- ----------------------------
DROP TABLE IF EXISTS `study_video_term`;
CREATE TABLE `study_video_term`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `video_id` bigint NOT NULL COMMENT '视频ID',
  `term_id` bigint NOT NULL COMMENT '术语ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_video_term`(`video_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `idx_video_id`(`video_id` ASC) USING BTREE,
  INDEX `idx_term_id`(`term_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '视频术语关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_video_term
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
