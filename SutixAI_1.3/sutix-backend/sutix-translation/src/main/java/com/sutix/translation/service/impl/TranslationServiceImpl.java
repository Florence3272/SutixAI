package com.sutix.translation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.translation.entity.TranslationRecord;
import com.sutix.translation.mapper.TranslationRecordMapper;
import com.sutix.translation.service.TranslationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TranslationServiceImpl extends ServiceImpl<TranslationRecordMapper, TranslationRecord>
        implements TranslationService {

    @Resource
    private TranslationRecordMapper recordMapper;

    @Override
    public List<TranslationRecord> listUserRecords(Long userId) {
        QueryWrapper<TranslationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public Long addRecord(TranslationRecord record) {
        save(record);
        return record.getId();
    }

    @Override
    public void updateResult(Long id, String resultText, String downloadUrl, Integer status) {
        TranslationRecord record = getById(id);
        if (record != null) {
            record.setResultText(resultText);
            record.setDownloadUrl(downloadUrl);
            record.setStatus(status);
            updateById(record);
        }
    }
}
