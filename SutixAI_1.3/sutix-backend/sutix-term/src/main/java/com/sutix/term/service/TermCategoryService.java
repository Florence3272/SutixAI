package com.sutix.term.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.term.dto.CategoryCountVO;
import com.sutix.term.entity.TermCategory;

import java.util.List;

public interface TermCategoryService extends IService<TermCategory> {
    /** 获取所有分类列表 */
    List<TermCategory> listAll();

    /** 新增分类 */
    boolean saveCategory(TermCategory termCategory);

    /** 修改分类 */
    boolean updateCategory(TermCategory termCategory);

    /** 删除分类（根据ID） */
    boolean removeCategory(Long id);

    /** 根据ID查询分类 */
    TermCategory getCategoryById(Long id);


}