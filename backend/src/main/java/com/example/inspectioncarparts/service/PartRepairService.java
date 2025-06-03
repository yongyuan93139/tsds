package com.example.inspectioncarparts.service;

import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.domain.entity.PartRepair;

/**
 * 配件维修服务接口
 */
public interface PartRepairService {

    /**
     * 创建维修工单
     *
     * @param partRepair 维修工单信息
     * @return 创建后的维修工单
     */
    PartRepair createRepair(PartRepair partRepair);

    /**
     * 更新维修工单
     *
     * @param partRepair 维修工单信息
     * @return 更新后的维修工单
     */
    PartRepair updateRepair(PartRepair partRepair);

    /**
     * 根据ID获取维修工单
     *
     * @param id 维修工单ID
     * @return 维修工单信息
     */
    PartRepair getRepairById(Long id);

    /**
     * 分页查询维修工单
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<PartRepair> listRepairs(int pageNum, int pageSize);

    /**
     * 处理维修工单
     *
     * @param id            维修工单ID
     * @param processResult 处理结果
     * @param faultReason   故障原因
     * @param operator      处理人
     * @param remark
     * @return 更新后的维修工单
     */
    PartRepair processRepair(Long id, String processResult, String faultReason, String operator);

    /**
     * 根据配件ID查询维修记录
     *
     * @param partId 配件ID
     * @return 维修记录列表
     */
    Page<PartRepair> listRepairsByPartId(Integer partId, int pageNum, int pageSize);
}
