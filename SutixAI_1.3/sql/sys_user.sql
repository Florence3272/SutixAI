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

 Date: 07/07/2026 14:17:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '1正常 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2C', '超级管理员', NULL, 1, 1, '2026-05-25 10:09:48', '2026-05-25 10:09:48');
INSERT INTO `sys_user` VALUES (2, 'shs', '$2a$10$uzKgF66uBW3TtNq4lIvYSepMV4njUbgX7X1/OIzxSP6SykjTbkIi2', 'shs', '', 1, 1, NULL, NULL);
INSERT INTO `sys_user` VALUES (6, 'tester', '$2a$10$.CwnvlyXcSViy1Pn.4d/iOWlreSqjOJtkxj4oteVcfi3LtSsE5pWu', 'Tester', NULL, 2, 1, NULL, NULL);
INSERT INTO `sys_user` VALUES (7, 'testuser2', '$2a$10$DsDfyoRCmNqxkGxnwsZRy.xf0QmwtDAVcpKWPo5qT8uRlZW/p854W', '测试用户', NULL, 2, 1, NULL, NULL);
INSERT INTO `sys_user` VALUES (8, 'testdemo', '$2a$10$4LGw2q2iPhZXHmOGvNZI0OtOqVGq.Xilhf2SPci76FzPyTCrl.OoG', '测试', NULL, 2, 1, NULL, NULL);
INSERT INTO `sys_user` VALUES (9, 'test', '$2a$10$P9cxfLuu3QZ1JCz6CJf0Ve3S6KeslSFHaqzCKj3ME5lCFZhroySri', 'test', '', 2, 1, NULL, NULL);
INSERT INTO `sys_user` VALUES (10, 'debuguser', '$2a$10$iDSlwJ8gEwnr7sxebSPoKOLqgyRMC9AmzWJbKhshF164yumS1CQt.', '调试', NULL, 2, 1, NULL, NULL);
INSERT INTO `sys_user` VALUES (11, 'finaltest', '$2a$10$F3JOroRBZNuT0WU4VUIiMOUwTPLOPPVZW8epe.HvPGsAFfKbjgFIi', '最终测试', NULL, 2, 1, NULL, NULL);
INSERT INTO `sys_user` VALUES (12, 'debugtest', '$2a$10$o5LiAtTvz8iQF6EhM0neAefTpSI4rdJ0tQ8ff6Pyq5nxyne1mlRu.', 'debugtest', NULL, 2, 1, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
