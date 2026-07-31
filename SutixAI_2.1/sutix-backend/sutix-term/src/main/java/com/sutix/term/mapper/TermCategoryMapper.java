package com.sutix.term.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sutix.term.dto.CategoryCountVO;
import com.sutix.term.entity.TermCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TermCategoryMapper extends BaseMapper<TermCategory> {

}