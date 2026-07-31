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

 Date: 20/07/2026 16:25:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for corpus_document
-- ----------------------------
DROP TABLE IF EXISTS `corpus_document`;
CREATE TABLE `corpus_document`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文档名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文档描述',
  `category_id` bigint NULL DEFAULT NULL COMMENT '关联分类ID',
  `create_user` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_create_user`(`create_user` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '语料文档表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of corpus_document
-- ----------------------------
INSERT INTO `corpus_document` VALUES (1, '智能制造概述', '机械工程领域智能制造相关专业语料', 1, 2, '2026-07-06 16:21:53', '2026-07-06 16:21:53');
INSERT INTO `corpus_document` VALUES (2, '电力系统基础', '电气工程领域电力系统基础语料', 2, 2, '2026-07-06 16:21:53', '2026-07-06 16:21:53');
INSERT INTO `corpus_document` VALUES (3, 'AI术语规范', '计算机科学领域人工智能术语语料', 5, 2, '2026-07-06 16:21:53', '2026-07-06 16:21:53');

SET FOREIGN_KEY_CHECKS = 1;
