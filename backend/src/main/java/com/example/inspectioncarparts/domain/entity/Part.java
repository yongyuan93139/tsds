package com.example.inspectioncarparts.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.example.inspectioncarparts.domain.enums.PartStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("part")
@ApiModel(description = "配件实体类")
public class Part {
    @ApiModelProperty(value = "配件ID", example = "1")
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @ApiModelProperty(value = "配件编号", example = "PART-001")
    @TableField("part_code")
    private String partCode;

    @ApiModelProperty(value = "资产编号", example = "ASSET-001")
    @TableField("asset_no")
    private String assetNo;

    @ApiModelProperty(value = "序列号", example = "SN-001")
    @TableField("serial_no")
    private String serialNo;

    @ApiModelProperty(value = "电子标签码", example = "RFID-001")
    @TableField("rfid_code")
    private String rfidCode;

    @ApiModelProperty(value = "单位", example = "个")
    @TableField("unit")
    private String unit;

    @ApiModelProperty(value = "规格型号", example = "型号A-100")
    @TableField("specification")
    private String specification;

    @ApiModelProperty(value = "品牌", example = "品牌A")
    @TableField("brand")
    private String brand;

    @ApiModelProperty(value = "创建人", example = "admin")
    @TableField("creator")
    private String creator;
    
    @ApiModelProperty(value = "父配件ID", example = "0")
    @TableField("parent_id")
    private Integer parentId;
    
    @ApiModelProperty(value = "配件类型ID", example = "1")
    @TableField("type_id")
    private Integer typeId;
    
    @ApiModelProperty(value = "所属车辆ID", example = "1")
    @TableField("vehicle_id")
    private Integer vehicleId;
    
    @ApiModelProperty(value = "生产日期", example = "2023-01-01")
    @TableField("production_date")
    private Date productionDate;
    
    @ApiModelProperty(value = "启用日期", example = "2023-01-15")
    @TableField("activation_date")
    private Date activationDate;
    
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 12:00:00")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @ApiModelProperty(value = "更新时间", example = "2023-01-01 12:00:00")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "配件状态", example = "1", notes = "参见PartStatus枚举")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "质保到期日期", example = "2024-01-01")
    @TableField("warranty_expiry_date")
    private Date warrantyExpiryDate;
}