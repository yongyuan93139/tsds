package com.example.inspectioncarparts.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("part_type")
public class PartType {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("type_code")
    private String typeCode;
    
    @TableField("type_name")
    private String typeName;
    
    @TableField("valid_days")
    private Integer validDays;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}