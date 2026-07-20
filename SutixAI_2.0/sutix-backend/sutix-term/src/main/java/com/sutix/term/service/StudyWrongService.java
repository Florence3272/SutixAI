package com.sutix.term.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.term.dto.WrongTermVO;
import com.sutix.term.entity.StudyWrong;

import java.util.List;

public interface StudyWrongService extends IService<StudyWrong> {
    /** 添加错题（若已存在则增加答错次数） */
    void addWrong(Long userId, Long termId);

    /** 获取用户错题列表（含术语详情） */
    List<WrongTermVO> listUserWrong(Long userId);

    /** 清空用户错题 */
    void clearUserWrong(Long userId);

    /** 移除单条错题 */
    void removeWrong(Long userId, Long termId);
}
