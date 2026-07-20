package com.sutix.term.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.term.entity.StudyVideo;
import com.sutix.term.entity.StudyVideoTerm;
import com.sutix.term.mapper.StudyVideoMapper;
import com.sutix.term.mapper.StudyVideoTermMapper;
import com.sutix.term.service.StudyVideoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyVideoServiceImpl extends ServiceImpl<StudyVideoMapper, StudyVideo>
        implements StudyVideoService {

    @Resource
    private StudyVideoMapper videoMapper;

    @Resource
    private StudyVideoTermMapper videoTermMapper;

    @Override
    public List<StudyVideo> listByCategory(Long categoryId) {
        QueryWrapper<StudyVideo> wrapper = new QueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        wrapper.orderByDesc("create_time");
        List<StudyVideo> videos = list(wrapper);
        // 为每个视频加载关联术语ID
        for (StudyVideo video : videos) {
            loadTermIds(video);
        }
        return videos;
    }

    @Override
    public StudyVideo getVideoDetail(Long videoId) {
        StudyVideo video = getById(videoId);
        if (video != null) {
            loadTermIds(video);
        }
        return video;
    }

    private void loadTermIds(StudyVideo video) {
        QueryWrapper<StudyVideoTerm> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", video.getId());
        List<StudyVideoTerm> relations = videoTermMapper.selectList(wrapper);
        List<Long> termIds = relations.stream()
                .map(StudyVideoTerm::getTermId)
                .collect(Collectors.toList());
        video.setTermIds(termIds);
    }
}
