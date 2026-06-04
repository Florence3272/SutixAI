package com.sutix.term.controller;

import com.sutix.common.result.Result;
import com.sutix.term.entity.TermCategory;
import com.sutix.term.service.TermCategoryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/term/category")
public class TermCategoryController {

    @Resource
    private TermCategoryService termCategoryService;

    /** 获取所有分类列表 */
    @GetMapping("/list")
    public Result<List<TermCategory>> list() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("当前认证对象: " + auth);
        System.out.println("当前用户名: " + auth.getName());
        System.out.println("是否已认证: " + auth.isAuthenticated());
        List<TermCategory> list = termCategoryService.listAll();
        return Result.success(list);
    }

    /** 根据ID查询分类 */
    @GetMapping("/{id}")
    public Result<TermCategory> getById(@PathVariable Long id) {
        TermCategory category = termCategoryService.getCategoryById(id);
        return Result.success(category);
    }

    /** 新增分类 */
    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody TermCategory termCategory) {
        boolean result = termCategoryService.saveCategory(termCategory);
        return Result.success(result);
    }

    /** 修改分类 */
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody TermCategory termCategory) {
        boolean result = termCategoryService.updateCategory(termCategory);
        return Result.success(result);
    }

    /** 删除分类 */
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        boolean result = termCategoryService.removeCategory(id);
        return Result.success(result);
    }
}