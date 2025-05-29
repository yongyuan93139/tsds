package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.inspectioncarparts.domain.entity.PartOperationHistory;
import com.example.inspectioncarparts.domain.enums.PartOperationType;
import com.example.inspectioncarparts.mapper.PartOperationHistoryMapper;
import com.example.inspectioncarparts.service.PartOperationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartOperationHistoryServiceImpl implements PartOperationHistoryService {

    @Autowired
    private PartOperationHistoryMapper partOperationHistoryMapper;

    @Override
    public PartOperationHistory recordOperation(Integer partId, PartOperationType operationType,
                                              String operator, String remark, Integer relatedId) {
        PartOperationHistory history = new PartOperationHistory();
        history.setPartId(partId);
        history.setOperationType(operationType.getCode());
        history.setOperationTime(LocalDateTime.now());
        history.setOperator(operator);
        history.setRemark(remark);
        history.setRelatedId(relatedId);

        partOperationHistoryMapper.insert(history);
        return history;
    }

    @Override
    public List<PartOperationHistory> getHistoryByPartId(Integer partId) {
        LambdaQueryWrapper<PartOperationHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartOperationHistory::getPartId, partId)
                   .orderByDesc(PartOperationHistory::getOperationTime);

        return partOperationHistoryMapper.selectList(queryWrapper);
    }

    @Override
    public List<PartOperationHistory> getHistoryByPartIdAndType(Integer partId, PartOperationType operationType) {
        LambdaQueryWrapper<PartOperationHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartOperationHistory::getPartId, partId)
                   .eq(PartOperationHistory::getOperationType, operationType.getCode())
                   .orderByDesc(PartOperationHistory::getOperationTime);

        return partOperationHistoryMapper.selectList(queryWrapper);
    }
}
