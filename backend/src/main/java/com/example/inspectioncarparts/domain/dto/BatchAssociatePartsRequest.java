package com.example.inspectioncarparts.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("批量关联配件请求")
public class BatchAssociatePartsRequest {

    @ApiModelProperty("车辆ID")
    private Integer vehicleId;

    @ApiModelProperty("配件ID列表")
    private List<Integer> partIds;
}
