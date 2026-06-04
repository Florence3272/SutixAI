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

 Date: 04/06/2026 18:36:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for term
-- ----------------------------
DROP TABLE IF EXISTS `term`;
CREATE TABLE `term`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '术语ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中文术语',
  `ru_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '俄文术语',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '术语解释',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `create_user` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_create_user`(`create_user` ASC) USING BTREE,
  INDEX `idx_name_ru_name`(`name` ASC, `ru_name` ASC) USING BTREE,
  CONSTRAINT `fk_term_category` FOREIGN KEY (`category_id`) REFERENCES `term_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_term_user` FOREIGN KEY (`create_user`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '术语表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of term
-- ----------------------------
INSERT INTO `term` VALUES (1, '国际贸易', 'Международная торговля', '跨越国境的商品与服务交换活动，是经济全球化重要组成部分。', 10, 2, '2026-05-25 12:55:38', '2026-06-04 15:27:51');
INSERT INTO `term` VALUES (2, '人工智能', 'Искусственный интеллект', '使机器模拟人类感知、推理、学习与决策的技术科学。', 5, 2, '2026-05-25 18:48:09', '2026-06-04 15:27:52');
INSERT INTO `term` VALUES (3, '生态环境', 'Экологическая среда', '生物群落与周边自然因素相互作用形成的整体生态系统。', 12, 2, '2026-05-25 18:48:51', '2026-06-04 15:27:53');
INSERT INTO `term` VALUES (4, '智能制造', 'Умное производство', '依托物联网、大数据与自动化实现工厂智能化生产模式。', 1, 2, '2026-05-25 18:49:35', NULL);

SET FOREIGN_KEY_CHECKS = 1;
