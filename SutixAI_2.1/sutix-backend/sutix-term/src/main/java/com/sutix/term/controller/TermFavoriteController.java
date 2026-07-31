package com.sutix.term.controller;

import com.sutix.common.result.Result;
import com.sutix.system.utils.SecurityUtils;
import com.sutix.term.service.TermFavoriteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/term/favorite")
public class TermFavoriteController {

    @Resource
    private TermFavoriteService favoriteService;

    // 收藏术语
    @PostMapping("/{termId}")
    public Result<Boolean> favorite(@PathVariable Long termId) {
        Long userId = SecurityUtils.getUserId();
        boolean success = favoriteService.addFavorite(userId, termId);
        return Result.success(success);
    }

    // 取消收藏
    @DeleteMapping("/{termId}")
    public Result<Boolean> unfavorite(@PathVariable Long termId) {
        Long userId = SecurityUtils.getUserId();
        boolean success = favoriteService.removeFavorite(userId, termId);
        return Result.success(success);
    }

    // 当前用户收藏的术语ID列表
    @GetMapping("/list")
    public Result<List<Long>> list() {
        Long userId = SecurityUtils.getUserId();
        List<Long> ids = favoriteService.listFavoriteTermIds(userId);
        return Result.success(ids);
    }

    @GetMapping("/count")
    public Result<Long> favoriteCount() {
        Long userId = SecurityUtils.getUserId();
        // 获取收藏ID列表
        List<Long> ids = favoriteService.listFavoriteTermIds(userId);
        return Result.success((long) ids.size());
    }
}