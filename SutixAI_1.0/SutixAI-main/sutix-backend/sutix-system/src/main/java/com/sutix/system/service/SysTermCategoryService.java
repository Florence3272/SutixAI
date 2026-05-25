package com.sutix.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.system.dto.CategoryDTO;
import com.sutix.system.entity.SysTermCategory;

import java.util.List;

public interface SysTermCategoryService extends IService<SysTermCategory> {
    // 新增/修改分类（管理员）
    void saveOrUpdateCategory(CategoryDTO categoryDTO, Long userId);
    // 删除分类（管理员）
    void deleteCategory(Long categoryId);
    // 查询所有分类（所有用户）
    List<SysTermCategory> listAllCategories();
}