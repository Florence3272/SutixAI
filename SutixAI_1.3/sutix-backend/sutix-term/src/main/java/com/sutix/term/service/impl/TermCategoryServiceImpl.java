package com.sutix.term.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.system.entity.SysUser;
import com.sutix.system.utils.SecurityUtils;
import com.sutix.term.dto.CategoryCountVO;
import com.sutix.term.entity.TermCategory;
import com.sutix.term.mapper.TermCategoryMapper;
import com.sutix.term.service.TermCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class TermCategoryServiceImpl extends ServiceImpl<TermCategoryMapper, TermCategory>
        implements TermCategoryService {

    /**
     * 查询所有分类列表
     */
    @Override
    public List<TermCategory> listAll() {
        return baseMapper.selectList(null);
    }

    /**
     * 根据ID查询单个分类
     */
    @Override
    public TermCategory getCategoryById(Long id) {
        if (id == null) {
            throw new RuntimeException("分类ID不能为空");
        }
        return getById(id);
    }

    /**
     * 新增术语分类
     */
    @Override
    public boolean saveCategory(TermCategory termCategory) {
        // 基础校验
        if (termCategory == null) {
            throw new RuntimeException("分类信息不能为空");
        }
        if (!StringUtils.hasText(termCategory.getCategoryName())) {
            throw new RuntimeException("分类名称不能为空");
        }

        SysUser user = SecurityUtils.getUser();
        termCategory.setCreateUser(user.getId());

        // 调用MyBatis-Plus保存方法
        return save(termCategory);
    }

    /**
     * 修改术语分类
     */
    @Override
    public boolean updateCategory(TermCategory termCategory) {
        // 基础校验
        if (termCategory == null) {
            throw new RuntimeException("分类信息不能为空");
        }
        if (termCategory.getId() == null) {
            throw new RuntimeException("分类ID不能为空");
        }
        if (!StringUtils.hasText(termCategory.getCategoryName())) {
            throw new RuntimeException("分类名称不能为空");
        }

        // 根据ID更新
        return updateById(termCategory);
    }

    /**
     * 删除术语分类
     */
    @Override
    public boolean removeCategory(Long id) {
        if (id == null) {
            throw new RuntimeException("分类ID不能为空");
        }

        // 根据ID删除
        return removeById(id);
    }

}
