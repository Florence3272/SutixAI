package com.sutix.term.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sutix.term.entity.Term;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Mapper
public interface TermMapper extends BaseMapper<Term> {
    /** 分页查询术语（联表查分类名称） */
    IPage<Term> selectTermPage(Page<Term> page,
                               @Param("keyword") String keyword,
                               @Param("categoryName") String categoryName);


    /** 查看术语详情（联表查分类名称、创建人名称） */
    Term selectTermDetail(@Param("id") Long id); // 新增方法
}
