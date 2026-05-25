package com.sutix.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.system.dto.TermAddDTO;
import com.sutix.system.dto.TermAuditDTO;
import com.sutix.system.dto.TermUpdateDTO;
import com.sutix.system.entity.SysTerm;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

public interface SysTermService extends IService<SysTerm> {

    // SysTermService.java新增接口
    /**
     * 导出术语到Excel
     * @param keyword 关键词筛选
     * @param categoryId 分类筛选
     * @return Excel文件流
     */
    void exportTerms(HttpServletResponse response, String keyword, Long categoryId) throws IOException;

    /**
     * 从Excel导入术语（仅管理员）
     * @param inputStream Excel文件流
     * @param userId 导入人ID
     */
    void importTerms(InputStream inputStream, Long userId);
    // 新增术语
    void addTerm(TermAddDTO termAddDTO, Long userId);

    // 分页查询术语（支持关键词搜索）
    IPage<SysTerm> listTerms(Page<SysTerm> page, String keyword, Long categoryId);

    // 管理员审核术语
    void auditTerm(TermAuditDTO termAuditDTO);
    // 修改术语（仅创建人+未审核可改）
    void updateTerm(TermUpdateDTO termUpdateDTO, Long userId);
    // 删除术语（仅创建人/管理员可删）
    void deleteTerm(Long termId, Long userId, boolean isAdmin);
}