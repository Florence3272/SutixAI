package com.sutix.term.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sutix.system.entity.SysUser;
import com.sutix.term.entity.Term;
import com.sutix.term.mapper.TermMapper;
import com.sutix.term.dto.TermQueryDTO;
import com.sutix.term.dto.TermFormDTO;
import com.sutix.term.service.TermService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.sutix.system.utils.SecurityUtils;

import javax.annotation.Resource;

@Service
public class TermServiceImpl extends ServiceImpl<TermMapper, Term> implements TermService {

    @Resource
    private TermMapper termMapper;

    // 当前登录用户
    private SysUser getLoginUser() {
        return SecurityUtils.getUser();
    }

    @Override
    public IPage<Term> pageList(TermQueryDTO queryDTO) {
        Page<Term> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        return termMapper.selectTermPage(page, queryDTO.getKeyword(), queryDTO.getCategoryName());
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

        boolean isAdmin = "admin".equals(user.getRole().getRoleKey());
        if (!isAdmin && !old.getCreateUser().equals(user.getId())) {
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

        boolean isAdmin = "admin".equals(user.getRole().getRoleKey());
        if (!isAdmin && !old.getCreateUser().equals(user.getId())) {
            throw new RuntimeException("无权限删除他人术语");
        }
        return removeById(id);
    }
}
