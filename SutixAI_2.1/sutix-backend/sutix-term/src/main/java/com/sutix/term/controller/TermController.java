package com.sutix.term.controller;

import com.sutix.common.annotation.Admin;
import com.sutix.common.result.Result;
import com.sutix.term.entity.Term;
import com.sutix.term.dto.TermQueryDTO;
import com.sutix.term.dto.TermFormDTO;
import com.sutix.term.service.TermService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/term")
public class TermController {

    @Resource
    private TermService termService;


    /** 分页查询术语列表 */
    @GetMapping("/list")
    public Result<IPage<Term>> list(TermQueryDTO queryDTO) {
        IPage<Term> page = termService.pageList(queryDTO);
        return Result.success(page);
    }

    /** 查看术语详情 */
    @GetMapping("/detail/{id}")
    public Result<Term> detail(@PathVariable Long id) {
        Term term = termService.getTermDetail(id);
        return Result.success(term);
    }

    /** 新增术语（管理员） */
    @Admin
    @PostMapping("/add")
    public Result<Boolean> add(@Validated @RequestBody TermFormDTO formDTO) {
        boolean success = termService.addTerm(formDTO);
        return success ? Result.success(true) : Result.fail("新增失败");
    }

    /** 编辑术语（管理员） */
    @Admin
    @PutMapping("/update")
    public Result<Boolean> update(@Validated @RequestBody TermFormDTO formDTO) {
        boolean success = termService.updateTerm(formDTO);
        return success ? Result.success(true) : Result.fail("编辑失败");
    }

    /** 删除术语（管理员） */
    @Admin
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = termService.deleteTerm(id);
        return success ? Result.success(true) : Result.fail("删除失败");
    }

    @Admin
    @PutMapping("/updateCategory")
    public Result<?> updateCategory(@RequestBody Term term) {
        termService.updateCategory(term.getId(), term.getCategoryId());
        return Result.success("移动成功");
    }
}
