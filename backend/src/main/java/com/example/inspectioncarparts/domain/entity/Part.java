package com.example.inspectioncarparts.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("part")
@ApiModel("配件实体")
public class Part {
    @ApiModelProperty("配件ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("配件编号")
    @TableField("part_code")
    private String partCode;

    @ApiModelProperty("资产编号")
    @TableField("asset_no")
    private String assetNo;

    @ApiModelProperty("序列号")
    @TableField("serial_no")
    private String serialNo;

    @ApiModelProperty("电子标签码")
    @TableField("rfid_code")
    private String rfidCode;

    @ApiModelProperty("单位")
    @TableField("unit")
    private String unit;

    @ApiModelProperty("规格型号")
    @TableField("specification")
    private String specification;

    @ApiModelProperty("品牌")
    @TableField("brand")
    private String brand;

    @ApiModelProperty("创建人")
    @TableField("creator")
    private String creator;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("父配件ID")
    @TableField("parent_id")
    private Integer parentId;

    @ApiModelProperty("配件类型ID")
    @TableField("type_id")
    private Integer typeId;

    @ApiModelProperty("所属车辆ID")
    @TableField("vehicle_id")
    private Integer vehicleId;

    @ApiModelProperty("生产日期")
    @TableField("production_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date productionDate;

    @ApiModelProperty("启用日期")
    @TableField("activation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date activationDate;

    @ApiModelProperty("质保到期日期")
    @TableField("warranty_expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date warrantyExpiryDate;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("配件状态: 0-禁用, 1-可用, 2-维修中, 3-报废")
    @TableField("status")
    private Integer status;
}
