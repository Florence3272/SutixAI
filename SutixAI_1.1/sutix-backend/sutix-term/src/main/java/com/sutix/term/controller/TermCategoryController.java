package com.sutix.term.controller;

import com.sutix.common.result.Result;
import com.sutix.term.entity.TermCategory;
import com.sutix.term.service.TermCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<TermCategory> list = termCategoryService.listAll();
        return Result.success(list);
    }
}