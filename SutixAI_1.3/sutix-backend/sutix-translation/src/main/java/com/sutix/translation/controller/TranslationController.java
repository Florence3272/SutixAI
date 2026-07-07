package com.sutix.translation.controller;

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
     * 获取翻译历史记录
     */
    @GetMapping("/history")
    public Result<List<TranslationRecord>> getHistory() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(translationService.listUserRecords(userId));
    }
}
