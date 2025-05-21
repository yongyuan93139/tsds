package com.example.inspectioncarparts.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PartStatus {
    DISABLED(0, "禁用"),
    ENABLED(1, "可用"),
    IN_REPAIR(2, "维修中"),
    SCRAPPED(3, "报废");

    @EnumValue
    private final int code;
    private final String description;

    PartStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PartStatus fromCode(int code) {
        for (PartStatus status : PartStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的配件状态码: " + code);
    }
}
