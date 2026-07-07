package com.sutix.term.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.term.entity.StudyVideo;
import java.util.List;

public interface StudyVideoService extends IService<StudyVideo> {
    /** 获取视频列表（按分类筛选） */
    List<StudyVideo> listByCategory(Long categoryId);

    /** 获取视频详情（包含关联术语ID列表） */
    StudyVideo getVideoDetail(Long videoId);
}
