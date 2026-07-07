package com.sutix.term.controller;

import com.sutix.common.result.Result;
import com.sutix.term.entity.StudyVideo;
import com.sutix.term.service.StudyVideoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/study/video")
public class StudyVideoController {

    @Resource
    private StudyVideoService videoService;

    /** 获取视频列表（可按分类筛选） */
    @GetMapping("/list")
    public Result<List<StudyVideo>> list(@RequestParam(required = false) Long categoryId) {
        return Result.success(videoService.listByCategory(categoryId));
    }

    /** 获取视频详情（含关联术语ID） */
    @GetMapping("/{id}")
    public Result<StudyVideo> detail(@PathVariable Long id) {
        return Result.success(videoService.getVideoDetail(id));
    }
}
