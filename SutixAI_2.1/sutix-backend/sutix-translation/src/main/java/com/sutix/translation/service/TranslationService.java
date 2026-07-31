package com.sutix.translation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.translation.entity.TranslationRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface TranslationService extends IService<TranslationRecord> {

    /** 获取用户翻译记录 */
    List<TranslationRecord> listUserRecords(Long userId);

    /**
     * 文本翻译（将文本封装为临时文件上传到 trans-home）
     * @param text     待翻译文本
     * @param direction 翻译方向 (zh2ru / ru2zh)
     * @param userId   当前用户 ID
     * @return TranslationRecord（含 ID、初始状态）
     */
    TranslationRecord translateText(String text, String direction, Long userId);

    /**
     * 上传文档翻译
     * @param file      上传的文件
     * @param direction 翻译方向
     * @param userId    当前用户 ID
     * @return TranslationRecord
     */
    TranslationRecord uploadDocument(MultipartFile file, String direction, Long userId);

    /**
     * 查询翻译进度，如果完成则下载结果并更新记录
     * @param id 本地记录 ID
     * @return 包含 status 和 resultText 的 Map
     */
    Map<String, Object> getProgress(Long id);

    /** 新增翻译记录 */
    Long addRecord(TranslationRecord record);

    /** 更新翻译结果 */
    void updateResult(Long id, String resultText, String downloadUrl, Integer status);
}
