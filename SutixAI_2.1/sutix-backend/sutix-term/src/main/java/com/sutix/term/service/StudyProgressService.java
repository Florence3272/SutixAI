package com.sutix.term.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.term.entity.StudyProgress;

import java.util.List;

public interface StudyProgressService extends IService<StudyProgress> {
    /** 标记术语学习状态 */
    void markStatus(Long userId, Long termId, String status);

    /** 获取用户指定术语的学习状态 */
    StudyProgress getProgress(Long userId, Long termId);

    /** 获取用户某个分类下的学习状态列表 */
    List<StudyProgress> listProgressByCategory(Long userId, Long categoryId);

    /** 获取用户某个状态的学习进度 */
    List<StudyProgress> listProgressByStatus(Long userId, String status);
}
