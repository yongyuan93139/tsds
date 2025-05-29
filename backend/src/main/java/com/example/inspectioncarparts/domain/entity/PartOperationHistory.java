package com.example.inspectioncarparts.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("part_operation_history")
@ApiModel(value = "配件操作历史")
public class PartOperationHistory {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("操作ID")
    private Integer id;

    @ApiModelProperty("配件ID")
    private Integer partId;

    @ApiModelProperty("操作类型：1-领用，2-关联车辆，3-解绑车辆，4-维修，5-报废")
    private Integer operationType;

    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("相关ID（如车辆ID）")
    private Integer relatedId;
}
