package com.example.inspectioncarparts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.inspectioncarparts.domain.entity.PartRepair;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配件维修Mapper接口
 */
@Mapper
public interface PartRepairMapper extends BaseMapper<PartRepair> {
    // 可以添加自定义的查询方法
}
