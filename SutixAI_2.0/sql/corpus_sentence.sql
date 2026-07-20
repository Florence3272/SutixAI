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

 Date: 20/07/2026 16:25:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for corpus_sentence
-- ----------------------------
DROP TABLE IF EXISTS `corpus_sentence`;
CREATE TABLE `corpus_sentence`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '语料行ID',
  `document_id` bigint NOT NULL COMMENT '所属文档ID',
  `zh_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中文原文',
  `ru_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '俄文译文',
  `seq` int NOT NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_document_id`(`document_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '双语语料行表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of corpus_sentence
-- ----------------------------
INSERT INTO `corpus_sentence` VALUES (1, 1, '智能制造是基于新一代信息技术的先进制造过程。', 'Интеллектуальное производство — это передовой производственный процесс на основе новых информационных технологий.', 1, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (2, 1, '自动化设备大幅提升了生产效率与产品质量。', 'Автоматизированное оборудование значительно повышает производительность и качество продукции.', 2, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (3, 1, '工业互联网实现设备之间的数据互通与协同作业。', 'Промышленный интернет обеспечивает обмен данными и совместную работу между устройствами.', 3, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (4, 1, '数字孪生技术通过虚拟模型实时映射物理设备状态。', 'Технология цифровых двойников в реальном времени отображает состояние физического оборудования через виртуальные модели.', 4, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (5, 2, '电力系统由发电、输电、配电和用电环节组成。', 'Энергетическая система состоит из генерации, передачи, распределения и потребления электроэнергии.', 1, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (6, 2, '高压输电线路能够减少远距离传输中的能量损耗。', 'Линии высоковольтной передачи снижают потери энергии при дистанционной транспортировке.', 2, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (7, 2, '变电站是电力系统中实现电压变换和电能分配的关键设施。', 'Подстанции являются ключевыми объектами для трансформации напряжения и распределения электроэнергии.', 3, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (8, 2, '智能电网通过通信技术优化电力供需平衡。', 'Умные сети оптимизируют баланс спроса и предложения электроэнергии с помощью коммуникационных технологий.', 4, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (9, 3, '人工智能是研究使计算机模拟人类智能的技术科学。', 'Искусственный интеллект — это техническая наука, исследующая моделирование человеческого интеллекта компьютерами.', 1, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (10, 3, '机器学习算法通过数据训练不断优化模型性能。', 'Алгоритмы машинного обучения постоянно оптимизируют производительность модели через обучение на данных.', 2, '2026-07-06 16:21:53');
INSERT INTO `corpus_sentence` VALUES (11, 3, '深度学习利用多层神经网络处理复杂的模式识别任务。', 'Глубокое обучение использует многослойные нейронные сети для решения сложных задач распознавания образов.', 3, '2026-07-06 16:21:53');

SET FOREIGN_KEY_CHECKS = 1;
