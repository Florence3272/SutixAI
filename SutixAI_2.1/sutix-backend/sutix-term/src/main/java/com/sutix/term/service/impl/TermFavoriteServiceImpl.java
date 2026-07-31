package com.sutix.term.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.term.entity.TermFavorite;
import com.sutix.term.mapper.TermFavoriteMapper;
import com.sutix.term.service.TermFavoriteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TermFavoriteServiceImpl extends ServiceImpl<TermFavoriteMapper, TermFavorite>
        implements TermFavoriteService {

    @Resource
    private TermFavoriteMapper favoriteMapper;

    @Override
    public boolean addFavorite(Long userId, Long termId) {
        QueryWrapper<TermFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("term_id", termId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            return true; // 已经收藏过
        }
        TermFavorite favorite = new TermFavorite();
        favorite.setUserId(userId);
        favorite.setTermId(termId);
        return save(favorite);
    }

    @Override
    public boolean removeFavorite(Long userId, Long termId) {
        QueryWrapper<TermFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("term_id", termId);
        return remove(wrapper);
    }

    @Override
    public List<Long> listFavoriteTermIds(Long userId) {
        return favoriteMapper.selectList(new QueryWrapper<TermFavorite>().eq("user_id", userId))
                .stream().map(TermFavorite::getTermId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorite(Long userId, Long termId) {
        QueryWrapper<TermFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("term_id", termId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }
}