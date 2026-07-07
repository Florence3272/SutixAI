package com.sutix.translation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.translation.entity.TranslationRecord;

import java.util.List;

public interface TranslationService extends IService<TranslationRecord> {
    /** 获取用户翻译记录 */
    List<TranslationRecord> listUserRecords(Long userId);

    /** 新增翻译记录 */
    Long addRecord(TranslationRecord record);

    /** 更新翻译结果 */
    void updateResult(Long id, String resultText, String downloadUrl, Integer status);
}
