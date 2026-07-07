package com.sutix.term.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sutix.system.entity.SysUser;
import com.sutix.term.dto.CategoryCountVO;
import com.sutix.term.entity.Term;
import com.sutix.term.mapper.TermMapper;
import com.sutix.term.dto.TermQueryDTO;
import com.sutix.term.dto.TermFormDTO;
import com.sutix.term.service.TermFavoriteService;
import com.sutix.term.service.TermService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.sutix.system.utils.SecurityUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TermServiceImpl extends ServiceImpl<TermMapper, Term> implements TermService {

    @Resource
    private TermMapper termMapper;
    @Resource
    private TermFavoriteService favoriteService;

    // 当前登录用户
    private SysUser getLoginUser() {
        return SecurityUtils.getUser();
    }

    @Override
    public IPage<Term> pageList(TermQueryDTO queryDTO) {
        // 预处理：去除关键词/分类名称的首尾空格
        String keyword = queryDTO.getKeyword() != null ? queryDTO.getKeyword().trim() : null;
        String categoryName = queryDTO.getCategoryName() != null ? queryDTO.getCategoryName().trim() : null;
        Page<Term> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        IPage<Term> result =
                termMapper.selectTermPage(page, keyword, categoryName,queryDTO.getCategoryId());

        Long userId = SecurityUtils.getUserId();
        List<Long> favIds =
                favoriteService.listFavoriteTermIds(userId);

        result.getRecords().forEach(term ->
                term.setIsLiked(
                        favIds.contains(term.getId())
                )
        );

        return result;
    }

    @Override
    public boolean addTerm(TermFormDTO formDTO) {
        Term term = new Term();
        BeanUtils.copyProperties(formDTO, term);
        term.setCreateUser(getLoginUser().getId());
        return save(term);
    }

    @Override
    public boolean updateTerm(TermFormDTO formDTO) {
        Term old = getById(formDTO.getId());
        SysUser user = getLoginUser();

        if (!old.getCreateUser().equals(user.getId())) {
            throw new RuntimeException("无权限编辑他人术语");
        }

        Term term = new Term();
        BeanUtils.copyProperties(formDTO, term);
        return updateById(term);
    }

    @Override
    public boolean deleteTerm(Long id) {
        Term old = getById(id);
        SysUser user = getLoginUser();

        if (!old.getCreateUser().equals(user.getId())) {
            throw new RuntimeException("无权限删除他人术语");
        }
        return removeById(id);
    }

    @Override
    public void updateCategory(Long id, Long categoryId) {
        Term term = new Term();
        term.setId(id);
        term.setCategoryId(categoryId); // 注意字段名是 categoryId
        updateById(term);
    }

    // 新增：实现详情查询方法
    @Override
    public Term getTermDetail(Long id) {
        Term term = termMapper.selectTermDetail(id);
        if (term == null) {
            throw new RuntimeException("术语不存在");
        }
        Long userId = SecurityUtils.getUserId();

        boolean liked =
                favoriteService.isFavorite(
                        userId,
                        term.getId()
                );

        term.setIsLiked(liked);

        return term;
    }

    @Override
    public List<CategoryCountVO> getCategoryCount() {
        // 调用 Mapper 查询数据库
        return termMapper.getCategoryCount();
    }
}
