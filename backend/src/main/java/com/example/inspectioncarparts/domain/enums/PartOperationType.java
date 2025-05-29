package com.example.inspectioncarparts.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配件操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum PartOperationType {

    RECEIVE(1, "配件领用"),
    ASSOCIATE_VEHICLE(2, "配件关联车辆"),
    DISASSOCIATE_VEHICLE(3, "配件和车辆解绑"),
    REPAIR(4, "配件维修"),
    SCRAP(5, "配件报废");

    private final Integer code;
    private final String description;

    /**
     * 根据代码获取枚举
     */
    public static PartOperationType getByCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (PartOperationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }

        return null;
    }
}
