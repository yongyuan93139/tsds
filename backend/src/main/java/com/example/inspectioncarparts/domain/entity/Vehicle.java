package com.example.inspectioncarparts.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("vehicle")
public class Vehicle {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("vehicle_code")
    private String vehicleCode;
    
    @TableField("vehicle_name")
    private String vehicleName;
    
    @TableField("gps_info")
    private String gpsInfo;
    
    @TableField("outbound_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date outboundTime;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}