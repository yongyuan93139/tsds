-- 创建配件操作历史表
CREATE TABLE part_operation_history (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '操作ID',
    part_id INT NOT NULL COMMENT '配件ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：RECEIVE(领用)、REPAIR(维修)、SCRAP(报废)等',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    remark VARCHAR(500) COMMENT '备注信息',
    related_id INT COMMENT '相关ID，如车辆ID等',

    -- 添加索引
    INDEX idx_part_operation_history_part_id (part_id),
    INDEX idx_part_operation_history_operation_time (operation_time),
    INDEX idx_part_operation_history_related_id (related_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配件操作历史表';

-- 添加初始数据（可选）
-- INSERT INTO part_operation_history (part_id, operation_type, operator, remark, related_id)
-- VALUES (1, 'RECEIVE', 'admin', '初始领用', NULL);
