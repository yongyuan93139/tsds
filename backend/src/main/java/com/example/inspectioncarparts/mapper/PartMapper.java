package com.example.inspectioncarparts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.inspectioncarparts.domain.entity.Part;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartMapper extends BaseMapper<Part> {
}