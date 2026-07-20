package com.sutix.term.controller;

import com.sutix.common.result.Result;
import com.sutix.system.utils.SecurityUtils;
import com.sutix.term.entity.StudyProgress;
import com.sutix.term.service.StudyProgressService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/study/progress")
public class StudyProgressController {

    @Resource
    private StudyProgressService progressService;

    /** 标记术语学习状态 */
    @PostMapping("/mark")
    public Result<Void> markStatus(@RequestParam Long termId, @RequestParam String status) {
        Long userId = SecurityUtils.getUserId();
        progressService.markStatus(userId, termId, status);
        return Result.success();
    }

    /** 获取用户指定术语的学习状态 */
    @GetMapping("/{termId}")
    public Result<StudyProgress> getProgress(@PathVariable Long termId) {
        Long userId = SecurityUtils.getUserId();
        StudyProgress progress = progressService.getProgress(userId, termId);
        return Result.success(progress);
    }

    /** 获取用户某个状态的学习进度列表 */
    @GetMapping("/list")
    public Result<List<StudyProgress>> listByStatus(@RequestParam(required = false) String status) {
        Long userId = SecurityUtils.getUserId();
        if (status != null && !status.isEmpty()) {
            return Result.success(progressService.listProgressByStatus(userId, status));
        }
        return Result.success(progressService.listProgressByCategory(userId, null));
    }
}
