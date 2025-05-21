/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.3.11-mysql
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : 192.168.3.11:3306
 Source Schema         : inspection_car_parts

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 20/05/2025 17:40:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for part
-- ----------------------------
DROP TABLE IF EXISTS `part`;
CREATE TABLE `part`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配件ID',
  `part_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配件编号',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父配件ID',
  `type_id` bigint NOT NULL COMMENT '配件类型ID',
  `vehicle_id` bigint NULL DEFAULT NULL COMMENT '所属车辆ID',
  `production_date` date NULL DEFAULT NULL COMMENT '生产时间',
  `activation_date` date NULL DEFAULT NULL COMMENT '启用时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '配件状态: 0-禁用, 1-可用, 2-维修中, 3-报废',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_part_code`(`part_code`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_type_id`(`type_id`) USING BTREE,
  INDEX `idx_vehicle_id`(`vehicle_id`) USING BTREE,
  CONSTRAINT `fk_part_parent` FOREIGN KEY (`parent_id`) REFERENCES `part` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_part_type` FOREIGN KEY (`type_id`) REFERENCES `part_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_part_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of part
-- ----------------------------
INSERT INTO `part` VALUES (1, 'TL01', NULL, 2, 1, '2025-05-20', NULL, '2025-05-20 10:53:28', '2025-05-20 10:53:28', 1);
INSERT INTO `part` VALUES (2, 'DZ01', NULL, 3, 1, NULL, NULL, '2025-05-20 10:53:55', '2025-05-20 10:53:55', 1);
INSERT INTO `part` VALUES (3, 'D', NULL, 2, 1, '2025-05-06', NULL, '2025-05-20 15:48:04', '2025-05-20 15:48:04', 0);

-- ----------------------------
-- Table structure for part_type
-- ----------------------------
DROP TABLE IF EXISTS `part_type`;
CREATE TABLE `part_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配件类型编号',
  `type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `valid_days` int NOT NULL COMMENT '有效期限(天)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_type_code`(`type_code`) USING BTREE,
  UNIQUE INDEX `idx_type_name`(`type_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配件类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of part_type
-- ----------------------------
INSERT INTO `part_type` VALUES (2, 'DZ', '对中', 30, '2025-05-20 08:48:49', '2025-05-20 08:48:49');
INSERT INTO `part_type` VALUES (3, '等待', '的', 30, '2025-05-20 09:48:52', '2025-05-20 09:48:52');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(加密存储)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:正常)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2025-05-19 12:21:36', '2025-05-20 08:43:48');

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `vehicle_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '车辆编号',
  `vehicle_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '车辆名称',
  `gps_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GPS位置信息',
  `outbound_time` datetime NULL DEFAULT NULL COMMENT '出库时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_vehicle_code`(`vehicle_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '车辆表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vehicle
-- ----------------------------
INSERT INTO `vehicle` VALUES (1, '其', '去', '', NULL, '2025-05-20 11:22:31', '2025-05-20 11:22:31');
INSERT INTO `vehicle` VALUES (2, '的', '的', '的', '2025-05-18 00:00:00', '2025-05-20 15:53:58', '2025-05-20 15:53:58');

SET FOREIGN_KEY_CHECKS = 1;
