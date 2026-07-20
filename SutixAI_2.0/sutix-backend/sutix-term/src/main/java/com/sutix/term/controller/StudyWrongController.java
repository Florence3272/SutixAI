package com.sutix.term.controller;

import com.sutix.common.result.Result;
import com.sutix.system.utils.SecurityUtils;
import com.sutix.term.dto.WrongTermVO;
import com.sutix.term.service.StudyWrongService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/study/wrong")
public class StudyWrongController {

    @Resource
    private StudyWrongService wrongService;

    /** 添加错题 */
    @PostMapping("/{termId}")
    public Result<Void> addWrong(@PathVariable Long termId) {
        Long userId = SecurityUtils.getUserId();
        wrongService.addWrong(userId, termId);
        return Result.success();
    }

    /** 获取用户错题列表（含术语详情） */
    @GetMapping("/list")
    public Result<List<WrongTermVO>> listWrong() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(wrongService.listUserWrong(userId));
    }

    /** 清空错题 */
    @DeleteMapping("/clear")
    public Result<Void> clearWrong() {
        Long userId = SecurityUtils.getUserId();
        wrongService.clearUserWrong(userId);
        return Result.success();
    }

    /** 移除单条错题 */
    @DeleteMapping("/{termId}")
    public Result<Void> removeWrong(@PathVariable Long termId) {
        Long userId = SecurityUtils.getUserId();
        wrongService.removeWrong(userId, termId);
        return Result.success();
    }
}
