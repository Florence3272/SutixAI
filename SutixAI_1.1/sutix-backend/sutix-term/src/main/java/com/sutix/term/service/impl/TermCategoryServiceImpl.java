package com.sutix.term.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.term.entity.TermCategory;
import com.sutix.term.mapper.TermCategoryMapper;
import com.sutix.term.service.TermCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TermCategoryServiceImpl extends ServiceImpl<TermCategoryMapper, TermCategory>
        implements TermCategoryService {

    @Override
    public List<TermCategory> listAll() {
        return baseMapper.selectList(null);
    }
}
