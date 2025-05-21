-- 为part表添加status字段
ALTER TABLE part
ADD COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '配件状态: 0-禁用, 1-可用, 2-维修中, 3-报废';

-- 更新现有记录的status字段（如果需要）
-- UPDATE part SET status = 1 WHERE status = 0;

-- 创建枚举类型的建议（Java代码中）
/*
public enum PartStatus {
    DISABLED(0, "禁用"),
    ENABLED(1, "可用"),
    IN_REPAIR(2, "维修中"),
    SCRAPPED(3, "报废");

    private final int code;
    private final String desc;

    // 构造函数、getter方法等
}
*/
