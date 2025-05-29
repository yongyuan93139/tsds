package com.example.inspectioncarparts.service;

import com.example.inspectioncarparts.domain.entity.PartOperationHistory;
import com.example.inspectioncarparts.domain.enums.PartOperationType;
import java.util.List;

public interface PartOperationHistoryService {

    /**
     * 记录配件操作历史
     * @param partId 配件ID
     * @param operationType 操作类型
     * @param operator 操作人
     * @param remark 备注
     * @param relatedId 相关ID（如车辆ID）
     * @return 操作历史记录
     */
    PartOperationHistory recordOperation(Integer partId, PartOperationType operationType,
                                        String operator, String remark, Integer relatedId);

    /**
     * 查询配件的操作历史
     * @param partId 配件ID
     * @return 操作历史列表
     */
    List<PartOperationHistory> getHistoryByPartId(Integer partId);

    /**
     * 查询特定类型的操作历史
     * @param partId 配件ID
     * @param operationType 操作类型
     * @return 操作历史列表
     */
    List<PartOperationHistory> getHistoryByPartIdAndType(Integer partId, PartOperationType operationType);
}
