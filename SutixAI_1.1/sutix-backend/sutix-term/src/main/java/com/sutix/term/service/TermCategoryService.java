package com.sutix.term.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.term.entity.TermCategory;

import java.util.List;

public interface TermCategoryService extends IService<TermCategory> {
    /** 获取所有分类列表 */
    List<TermCategory> listAll();
}