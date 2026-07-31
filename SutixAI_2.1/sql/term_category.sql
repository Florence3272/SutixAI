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

 Date: 20/07/2026 16:26:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for term_category
-- ----------------------------
DROP TABLE IF EXISTS `term_category`;
CREATE TABLE `term_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `create_user` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_name`(`category_name` ASC) USING BTREE,
  INDEX `idx_create_user`(`create_user` ASC) USING BTREE,
  CONSTRAINT `fk_category_user` FOREIGN KEY (`create_user`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '术语分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of term_category
-- ----------------------------
INSERT INTO `term_category` VALUES (1, '机械工程', '📚', 1, '2026-05-25 12:55:30', NULL);
INSERT INTO `term_category` VALUES (2, '电气工程', '⚡', 1, '2026-05-25 18:40:47', '2026-06-01 10:21:11');
INSERT INTO `term_category` VALUES (3, '化学化工', '🧪', 1, '2026-05-25 18:40:58', '2026-06-01 10:21:17');
INSERT INTO `term_category` VALUES (4, '医药卫生', '💊', 1, '2026-05-25 18:41:26', '2026-06-01 10:21:24');
INSERT INTO `term_category` VALUES (5, '计算机科学', '💻', 1, '2026-05-25 18:41:37', '2026-06-01 10:21:31');
INSERT INTO `term_category` VALUES (6, '建筑工程', '🏗️', 1, '2026-05-25 18:41:49', '2026-06-01 10:21:38');
INSERT INTO `term_category` VALUES (7, '交通运输', '🚄', 1, '2026-05-25 18:42:09', '2026-06-01 10:21:43');
INSERT INTO `term_category` VALUES (8, '能源动力', '🔋', 1, '2026-05-25 18:42:20', '2026-06-01 10:21:49');
INSERT INTO `term_category` VALUES (9, '法律法务', '⚖️', 1, '2026-05-25 18:42:30', '2026-06-01 10:21:54');
INSERT INTO `term_category` VALUES (10, '经济贸易', '💰', 1, '2026-05-25 18:44:06', '2026-06-01 10:21:58');
INSERT INTO `term_category` VALUES (11, '农业科学', '🌾', 1, '2026-05-25 18:44:12', '2026-06-01 10:22:03');
INSERT INTO `term_category` VALUES (12, '环境科学', '🌍', 1, '2026-05-25 18:44:29', '2026-06-01 13:37:40');

SET FOREIGN_KEY_CHECKS = 1;
