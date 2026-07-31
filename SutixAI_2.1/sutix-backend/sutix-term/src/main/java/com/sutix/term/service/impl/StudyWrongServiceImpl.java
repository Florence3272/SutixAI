package com.sutix.term.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.term.dto.WrongTermVO;
import com.sutix.term.entity.StudyWrong;
import com.sutix.term.entity.Term;
import com.sutix.term.mapper.StudyWrongMapper;
import com.sutix.term.mapper.TermMapper;
import com.sutix.term.service.StudyWrongService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudyWrongServiceImpl extends ServiceImpl<StudyWrongMapper, StudyWrong>
        implements StudyWrongService {

    @Resource
    private StudyWrongMapper wrongMapper;

    @Resource
    private TermMapper termMapper;

    @Override
    public void addWrong(Long userId, Long termId) {
        QueryWrapper<StudyWrong> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("term_id", termId);
        StudyWrong existing = wrongMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setWrongCount(existing.getWrongCount() + 1);
            existing.setLastWrongTime(LocalDateTime.now());
            updateById(existing);
        } else {
            StudyWrong wrong = new StudyWrong();
            wrong.setUserId(userId);
            wrong.setTermId(termId);
            wrong.setWrongCount(1);
            wrong.setLastWrongTime(LocalDateTime.now());
            save(wrong);
        }
    }

    @Override
    public List<WrongTermVO> listUserWrong(Long userId) {
        QueryWrapper<StudyWrong> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("update_time");
        List<StudyWrong> wrongs = list(wrapper);

        List<WrongTermVO> result = new ArrayList<>();
        for (StudyWrong w : wrongs) {
            Term term = termMapper.selectTermDetail(w.getTermId());
            if (term != null) {
                WrongTermVO vo = new WrongTermVO();
                BeanUtils.copyProperties(term, vo);
                vo.setId(w.getId());
                vo.setTermId(term.getId());
                vo.setUserId(w.getUserId());
                vo.setWrongCount(w.getWrongCount());
                vo.setLastWrongTime(w.getLastWrongTime());
                vo.setCreateTime(w.getCreateTime());
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public void clearUserWrong(Long userId) {
        QueryWrapper<StudyWrong> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        remove(wrapper);
    }

    @Override
    public void removeWrong(Long userId, Long termId) {
        QueryWrapper<StudyWrong> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("term_id", termId);
        remove(wrapper);
    }
}
