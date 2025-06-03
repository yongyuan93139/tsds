package com.example.inspectioncarparts.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("part_repair")
@ApiModel(description = "配件维修实体")
public class PartRepair {

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @ApiModelProperty("维修工单号")
    @TableField("repair_order_no")
    private String repairOrderNo;

    @ApiModelProperty("维修配件ID")
    @TableField("part_id")
    private Integer partId;

    @ApiModelProperty("报障时间")
    @TableField("fault_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime faultTime;

    @ApiModelProperty("故障描述")
    @TableField("fault_description")
    private String faultDescription;

    @ApiModelProperty("工单状态：0-待处理，1-已处理")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("处理时间")
    @TableField("process_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processTime;

    @ApiModelProperty("处理结果")
    @TableField("process_result")
    private String processResult;

    @ApiModelProperty("故障原因")
    @TableField("fault_reason")
    private String faultReason;

    @ApiModelProperty("附件URL列表，多个URL用逗号分隔")
    @TableField("attachments")
    private String attachments;

    @ApiModelProperty("创建人")
    @TableField("creator")
    private String creator;

    @ApiModelProperty("处理人")
    @TableField("operator")
    private String operator;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;
}
