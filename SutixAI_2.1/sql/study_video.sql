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

 Date: 20/07/2026 16:25:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for study_video
-- ----------------------------
DROP TABLE IF EXISTS `study_video`;
CREATE TABLE `study_video`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '视频描述',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频地址',
  `poster` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图',
  `category_id` bigint NULL DEFAULT NULL COMMENT '关联分类ID',
  `duration` int NULL DEFAULT NULL COMMENT '视频时长(秒)',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习视频表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of study_video
-- ----------------------------
INSERT INTO `study_video` VALUES (1, '机械工程基础术语讲解', '机械工程领域常用中俄术语解读', 'https://example.com/videos/mech1.mp4', 'https://via.placeholder.com/320x180/409eff/ffffff?text=Engineering', 1, 480, 2, '2026-07-06 16:00:59', '2026-07-06 16:00:59');
INSERT INTO `study_video` VALUES (2, '计算机科学核心术语', '计算机科学与人工智能方向术语学习', 'https://example.com/videos/cs1.mp4', 'https://via.placeholder.com/320x180/8e44ad/ffffff?text=Computer', 5, 360, 2, '2026-07-06 16:00:59', '2026-07-06 16:00:59');
INSERT INTO `study_video` VALUES (3, '经济贸易词汇精讲', '国际贸易与金融领域专业俄语词汇', 'https://example.com/videos/econ1.mp4', 'https://via.placeholder.com/320x180/f39c12/ffffff?text=Economy', 10, 540, 2, '2026-07-06 16:00:59', '2026-07-06 16:00:59');

SET FOREIGN_KEY_CHECKS = 1;
