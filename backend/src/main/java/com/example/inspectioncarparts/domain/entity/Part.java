package com.example.inspectioncarparts.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.example.inspectioncarparts.domain.enums.PartStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
@TableName("part")
public class Part {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("part_code")
    private String partCode;
    
    @TableField("parent_id")
    private Long parentId;
    
    @TableField("type_id")
    private Long typeId;
    
    @TableField("vehicle_id")
    private Long vehicleId;
    
    @TableField("production_date")
    private Date productionDate;
    
    @TableField("activation_date")
    private Date activationDate;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 配件状态
     * @see com.example.inspectioncarparts.domain.enums.PartStatus
     */
    @TableField("status")
    private Integer status;
}