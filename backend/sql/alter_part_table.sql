-- 为配件表增加新字段
ALTER TABLE `part`
ADD COLUMN `asset_no` varchar(50) DEFAULT NULL COMMENT '资产编号' AFTER `part_code`,
ADD COLUMN `serial_no` varchar(50) DEFAULT NULL COMMENT '序列号' AFTER `asset_no`,
ADD COLUMN `rfid_code` varchar(50) DEFAULT NULL COMMENT '电子标签码' AFTER `serial_no`,
ADD COLUMN `unit` varchar(20) DEFAULT NULL COMMENT '单位' AFTER `rfid_code`,
ADD COLUMN `specification` varchar(100) DEFAULT NULL COMMENT '规格型号' AFTER `unit`,
ADD COLUMN `brand` varchar(50) DEFAULT NULL COMMENT '品牌' AFTER `specification`,
ADD COLUMN `brand` varchar(50) DEFAULT NULL COMMENT '品牌' AFTER `specification`,

ADD COLUMN `creator` varchar(50) DEFAULT NULL COMMENT '创建人' AFTER `brand`;

-- 添加索引以提高查询性能
ALTER TABLE `part`
ADD INDEX `idx_asset_no` (`asset_no`),
ADD INDEX `idx_serial_no` (`serial_no`),
ADD INDEX `idx_rfid_code` (`rfid_code`);
