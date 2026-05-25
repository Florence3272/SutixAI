package com.sutix.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.system.dto.TermAddDTO;
import com.sutix.system.dto.TermAuditDTO;
import com.sutix.system.dto.TermUpdateDTO;
import com.sutix.system.entity.SysTerm;
import com.sutix.system.entity.SysTermCategory;
import com.sutix.system.excel.TermExcelDTO;
import com.sutix.system.mapper.SysTermMapper;
import com.sutix.system.service.SysTermCategoryService;
import com.sutix.system.service.SysTermService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysTermServiceImpl extends ServiceImpl<SysTermMapper, SysTerm> implements SysTermService {

    // 注入分类Service（用于匹配分类名称和ID）
    private final SysTermCategoryService categoryService;
    public SysTermServiceImpl(SysTermCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 新增术语
    @Override
    public void addTerm(TermAddDTO termAddDTO, Long userId) {
        SysTerm term = new SysTerm();
        term.setTermName(termAddDTO.getTermName());
        term.setTermExplain(termAddDTO.getTermExplain());
        term.setCategoryId(termAddDTO.getCategoryId());
        term.setCreateUserId(userId);
        term.setStatus(0); // 默认为待审核
        this.save(term);
    }

    // 分页查询术语（支持关键词搜索）
    @Override
    public IPage<SysTerm> listTerms(Page<SysTerm> page, String keyword, Long categoryId) { // 新增categoryId参数
        LambdaQueryWrapper<SysTerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTerm::getDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysTerm::getTermName, keyword);
        }
        if (categoryId != null && categoryId > 0) { // 分类筛选
            wrapper.eq(SysTerm::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(SysTerm::getCreateTime);
        return this.page(page, wrapper);
    }

    // 管理员审核术语
    @Override
    public void auditTerm(TermAuditDTO termAuditDTO) {
        SysTerm term = this.getById(termAuditDTO.getTermId());
        if (term == null) {
            throw new RuntimeException("术语不存在");
        }
        // 驳回时必须填写原因
        if (termAuditDTO.getStatus() == 2 && !StringUtils.hasText(termAuditDTO.getRejectReason())) {
            throw new RuntimeException("驳回术语必须填写驳回原因");
        }
        term.setStatus(termAuditDTO.getStatus());
        term.setRejectReason(termAuditDTO.getRejectReason());
        this.updateById(term);
    }

    // 修改术语（仅创建人+未审核可改）
    @Override
    public void updateTerm(TermUpdateDTO termUpdateDTO, Long userId) {
        SysTerm term = this.getById(termUpdateDTO.getId());
        if (term == null) {
            throw new RuntimeException("术语不存在");
        }
        // 校验：仅创建人可改 + 术语未审核
        if (!term.getCreateUserId().equals(userId)) {
            throw new RuntimeException("仅创建人可修改该术语");
        }
        if (term.getStatus() != 0) {
            throw new RuntimeException("仅待审核的术语可修改");
        }
        // 更新字段
        term.setTermName(termUpdateDTO.getTermName());
        term.setTermExplain(termUpdateDTO.getTermExplain());
        term.setCategoryId(termUpdateDTO.getCategoryId());
        this.updateById(term);
    }

    // 删除术语（仅创建人/管理员可删）
    @Override
    public void deleteTerm(Long termId, Long userId, boolean isAdmin) {
        SysTerm term = this.getById(termId);
        if (term == null) {
            throw new RuntimeException("术语不存在");
        }
        // 校验权限：创建人 或 管理员
        if (!term.getCreateUserId().equals(userId) && !isAdmin) {
            throw new RuntimeException("仅创建人或管理员可删除该术语");
        }
        this.removeById(termId); // 逻辑删除（@TableLogic 自动处理）
    }

    // ========== Excel导出 ==========
    @Override
    public void exportTerms(HttpServletResponse response, String keyword, Long categoryId) throws IOException {
        // 1. 查询要导出的术语列表
        LambdaQueryWrapper<SysTerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysTerm::getDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysTerm::getTermName, keyword);
        }
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(SysTerm::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(SysTerm::getCreateTime);
        List<SysTerm> termList = this.list(wrapper);

        // 2. 获取分类名称映射（分类ID→分类名称）
        List<SysTermCategory> categoryList = categoryService.listAllCategories();
        Map<Long, String> categoryMap = categoryList.stream()
                .collect(Collectors.toMap(SysTermCategory::getId, SysTermCategory::getCategoryName));

        // 3. 转换为ExcelDTO
        List<TermExcelDTO> excelList = termList.stream().map(term -> {
            TermExcelDTO dto = new TermExcelDTO();
            dto.setTermName(term.getTermName());
            dto.setTermExplain(term.getTermExplain());
            dto.setCategoryName(categoryMap.getOrDefault(term.getCategoryId(), "未分类"));
            return dto;
        }).collect(Collectors.toList());

        // 4. 设置响应头（下载Excel）
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("术语列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 5. 写入Excel
        EasyExcel.write(response.getOutputStream(), TermExcelDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动适配列宽
                .sheet("术语列表")
                .doWrite(excelList);
    }

    // ========== Excel导入 ==========
    @Override
    public void importTerms(InputStream inputStream, Long userId) {
        // 1. 获取分类名称映射（分类名称→分类ID）
        List<SysTermCategory> categoryList = categoryService.listAllCategories();
        Map<String, Long> categoryMap = categoryList.stream()
                .collect(Collectors.toMap(SysTermCategory::getCategoryName, SysTermCategory::getId, (k1, k2) -> k1));

        // 2. 读取Excel并导入
        List<TermExcelDTO> excelList = EasyExcel.read(inputStream)
                .head(TermExcelDTO.class)
                .sheet()
                .doReadSync();

        // 3. 批量处理
        for (TermExcelDTO dto : excelList) {
            // 校验术语名称非空
            if (!StringUtils.hasText(dto.getTermName())) {
                throw new RuntimeException("导入失败：存在空的术语名称");
            }
            // 匹配分类ID（无匹配则设为0）
            Long categoryId = categoryMap.getOrDefault(dto.getCategoryName(), 0L);

            // 封装为TermAddDTO并保存
            TermAddDTO addDTO = new TermAddDTO();
            addDTO.setTermName(dto.getTermName());
            addDTO.setTermExplain(dto.getTermExplain());
            addDTO.setCategoryId(categoryId);
            this.addTerm(addDTO, userId); // 复用原有新增方法
        }
    }
}