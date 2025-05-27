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

 Date: 22/05/2025 18:12:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for part
-- ----------------------------
DROP TABLE IF EXISTS `part`;
CREATE TABLE `part`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '配件ID',
  `part_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配件编号',
  `parent_id` int NULL DEFAULT NULL COMMENT '父配件ID',
  `type_id` int NOT NULL COMMENT '配件类型ID',
  `vehicle_id` int NULL DEFAULT NULL COMMENT '所属车辆ID',
  `production_date` date NULL DEFAULT NULL COMMENT '生产时间',
  `activation_date` date NULL DEFAULT NULL COMMENT '启用时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '配件状态: 0-禁用, 1-可用, 2-维修中, 3-报废',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_part_code`(`part_code`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_type_id`(`type_id`) USING BTREE,
  INDEX `idx_vehicle_id`(`vehicle_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of part
-- ----------------------------
INSERT INTO `part` VALUES (1, 'DZ25052101', NULL, 2, 1, '2025-05-20', NULL, '2025-05-20 10:53:28', '2025-05-21 16:01:21', 0);
INSERT INTO `part` VALUES (2, 'QQ25052101', NULL, 3, NULL, '2025-05-25', NULL, '2025-05-20 10:53:55', '2025-05-21 16:04:08', 2);
INSERT INTO `part` VALUES (3, 'TL25052102', NULL, 5, 1, '2025-05-06', NULL, '2025-05-20 15:48:04', '2025-05-21 14:26:24', 0);
INSERT INTO `part` VALUES (4, ' DZ25052102', NULL, 2, NULL, '2025-05-04', NULL, '2025-05-21 14:25:51', '2025-05-21 14:25:51', 1);

-- ----------------------------
-- Table structure for part_type
-- ----------------------------
DROP TABLE IF EXISTS `part_type`;
CREATE TABLE `part_type`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配件类型编号',
  `type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `valid_days` int NOT NULL COMMENT '有效期限(天)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_type_code`(`type_code`) USING BTREE,
  UNIQUE INDEX `idx_type_name`(`type_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '配件类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of part_type
-- ----------------------------
INSERT INTO `part_type` VALUES (2, 'DUI_ZHONG', '对中', 29, '2025-05-20 08:48:49', '2025-05-20 08:48:49');
INSERT INTO `part_type` VALUES (3, 'QIAN_QIAO', '前桥', 30, '2025-05-20 09:48:52', '2025-05-20 09:48:52');
INSERT INTO `part_type` VALUES (5, 'TAN_LUN', '探轮', 30, '2025-05-21 14:23:48', '2025-05-21 14:23:48');
INSERT INTO `part_type` VALUES (6, 'JIN_PIAN', '晶片', 30, '2025-05-21 14:24:07', '2025-05-21 14:24:07');
INSERT INTO `part_type` VALUES (7, 'ECU', 'ECU控制板', 30, '2025-05-21 14:24:30', '2025-05-21 14:24:30');

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限标识（如user:add）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` tinyint NULL DEFAULT NULL COMMENT '1-菜单, 2-接口, 3-按钮',
  `parent_id` int NULL DEFAULT 0 COMMENT '父权限ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (5, 'system', '系统管理', 1, 0);
INSERT INTO `permission` VALUES (6, 'user', '用户管理', 1, 5);
INSERT INTO `permission` VALUES (7, 'user:add', '新增用户', 3, 6);
INSERT INTO `permission` VALUES (8, 'user:delete', '删除用户', 3, 6);
INSERT INTO `permission` VALUES (9, 'user:update', '编辑用户', 3, 6);
INSERT INTO `permission` VALUES (10, 'user:query', '查询用户', 3, 6);
INSERT INTO `permission` VALUES (11, 'user:role', '角色配置', 3, 6);
INSERT INTO `permission` VALUES (12, 'role', '角色管理', 1, 5);
INSERT INTO `permission` VALUES (13, 'role:add', '新增角色', 3, 12);
INSERT INTO `permission` VALUES (14, 'role:delete', '删除角色', 3, 12);
INSERT INTO `permission` VALUES (15, 'role:update', '编辑角色', 3, 12);
INSERT INTO `permission` VALUES (16, 'role:query', '查询角色', 3, 12);
INSERT INTO `permission` VALUES (17, 'role:permission', '权限配置', 3, 12);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '系统管理员', '系统管理');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `role_id` int NOT NULL,
  `permission_id` int NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1, 5);
INSERT INTO `role_permission` VALUES (1, 6);
INSERT INTO `role_permission` VALUES (1, 7);
INSERT INTO `role_permission` VALUES (1, 8);
INSERT INTO `role_permission` VALUES (1, 9);
INSERT INTO `role_permission` VALUES (1, 10);
INSERT INTO `role_permission` VALUES (1, 11);
INSERT INTO `role_permission` VALUES (1, 12);
INSERT INTO `role_permission` VALUES (1, 13);
INSERT INTO `role_permission` VALUES (1, 14);
INSERT INTO `role_permission` VALUES (1, 15);
INSERT INTO `role_permission` VALUES (1, 16);
INSERT INTO `role_permission` VALUES (1, 17);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(加密存储)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:正常)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, '2025-05-19 12:21:36', '2025-05-20 08:43:48', NULL);
INSERT INTO `sys_user` VALUES (2, 'luhuaping', '$2a$10$aIgaTQ1lxZcCvU2BTGzkCOzPVeBfQrHuMAq46EDx/PVisq8PsarE6', 1, '2025-05-22 11:12:59', '2025-05-22 11:12:59', '卢华萍');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (2, 1);

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `vehicle_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '车辆编号',
  `vehicle_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '车辆名称',
  `gps_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GPS位置信息',
  `outbound_time` datetime NULL DEFAULT NULL COMMENT '出库时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_vehicle_code`(`vehicle_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '车辆表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vehicle
-- ----------------------------
INSERT INTO `vehicle` VALUES (1, 'FD01', '富岛标定线小车', '', '2025-05-20 00:00:00', '2025-05-20 11:22:31', '2025-05-20 11:22:31');
INSERT INTO `vehicle` VALUES (2, 'NN', '南宁小车', '的', '2025-05-18 00:00:00', '2025-05-20 15:53:58', '2025-05-20 15:53:58');

SET FOREIGN_KEY_CHECKS = 1;
