package com.sutix.translation.controller;

import com.sutix.common.annotation.Admin;
import com.sutix.common.result.Result;
import com.sutix.system.utils.SecurityUtils;
import com.sutix.translation.entity.TranslationRecord;
import com.sutix.translation.service.FileParseService;
import com.sutix.translation.service.TranslationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/translation")
@Admin
public class TranslationController {

    @Resource
    private TranslationService translationService;

    @Resource
    private FileParseService fileParseService;

    /**
     * 解析文件内容（支持 .txt / .doc / .docx / .pdf）
     */
    @PostMapping("/parse")
    public Result<Map<String, String>> parseFile(@RequestParam("file") MultipartFile file) {
        try {
            String text = fileParseService.parseFile(file);
            Map<String, String> result = new HashMap<>();
            result.put("text", text);
            result.put("fileName", file.getOriginalFilename());
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("文件解析失败：" + e.getMessage());
        }
    }

    /**
     * 文本翻译（将文本封装后调用 trans-home API）
     * 请求参数: text, direction (zh2ru / ru2zh)
     * 返回: TranslationRecord（含 id、初始 status=0）
     */
    @PostMapping("/translate")
    public Result<TranslationRecord> translateText(@RequestParam String text,
                                                   @RequestParam(defaultValue = "zh2ru") String direction) {
        if (text == null || text.trim().isEmpty()) {
            return Result.fail("翻译文本不能为空");
        }
        if (text.length() > 100000) {
            return Result.fail("文本过长，请拆分后翻译（最大 100000 字符）");
        }
        try {
            Long userId = SecurityUtils.getUserId();
            TranslationRecord record = translationService.translateText(text, direction, userId);
            return Result.success(record);
        } catch (Exception e) {
            return Result.fail("翻译提交失败：" + e.getMessage());
        }
    }

    /**
     * 上传文档翻译
     */
    @PostMapping("/upload")
    public Result<TranslationRecord> uploadDocument(@RequestParam("file") MultipartFile file,
                                                    @RequestParam(defaultValue = "zh2ru") String direction) {
        if (file == null || file.isEmpty()) {
            return Result.fail("请选择要上传的文件");
        }
        try {
            Long userId = SecurityUtils.getUserId();
            TranslationRecord record = translationService.uploadDocument(file, direction, userId);
            return Result.success(record);
        } catch (Exception e) {
            return Result.fail("文档上传翻译失败：" + e.getMessage());
        }
    }

    /**
     * 查询翻译进度
     * 返回: { status: -1/0/1, resultText: "译文", downloadUrl: "..." }
     */
    @GetMapping("/progress/{id}")
    public Result<Map<String, Object>> getProgress(@PathVariable Long id) {
        try {
            Map<String, Object> progress = translationService.getProgress(id);
            return Result.success(progress);
        } catch (Exception e) {
            return Result.fail("查询进度失败：" + e.getMessage());
        }
    }

    /**
     * 获取翻译历史记录
     */
    @GetMapping("/history")
    public Result<List<TranslationRecord>> getHistory() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(translationService.listUserRecords(userId));
    }
}
