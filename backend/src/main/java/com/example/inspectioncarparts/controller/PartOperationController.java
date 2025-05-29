package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.domain.entity.PartOperationHistory;
import com.example.inspectioncarparts.service.PartOperationHistoryService;
import com.example.inspectioncarparts.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配件操作控制器
 * 提供配件领用、维修、报废等操作的API接口
 */
@RestController
@RequestMapping("/api/parts/operations")
public class PartOperationController {

    @Autowired
    private PartService partService;

    @Autowired
    private PartOperationHistoryService partOperationHistoryService;

    /**
     * 配件领用接口
     * 
     * @param partId 配件ID
     * @param operator 操作人
     * @return 更新后的配件信息
     */
    @PostMapping("/{partId}/receive")
    public Result<Part> receivePart(
            @PathVariable Integer partId,
            @RequestParam String operator) {
        try {
            Part part = partService.receivePart(partId, operator);
            return Result.success(part);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 配件维修接口
     * 
     * @param partId 配件ID
     * @param operator 操作人
     * @param remark 维修备注
     * @return 更新后的配件信息
     */
    @PostMapping("/{partId}/repair")
    public Result<Part> repairPart(
            @PathVariable Integer partId,
            @RequestParam String operator,
            @RequestParam String remark) {
        try {
            Part part = partService.repairPart(partId, operator, remark);
            return Result.success(part);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 配件报废接口
     * 
     * @param partId 配件ID
     * @param remark 报废原因
     * @return 更新后的配件信息
     */
    @PostMapping("/{partId}/scrap")
    public Result<Part> scrapPart(
            @PathVariable Integer partId,
            @RequestParam String remark) {
        Part part = partService.scrapPart(partId, remark);
        return Result.success(part);
    }

    /**
     * 查询配件操作历史接口
     * 
     * @param partId 配件ID
     * @return 配件的操作历史记录列表
     */
    @GetMapping("/{partId}/history")
    public Result<List<PartOperationHistory>> getPartHistory(@PathVariable Integer partId) {
        try {
            List<PartOperationHistory> history = partOperationHistoryService.getHistoryByPartId(partId);
            return Result.success(history);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
