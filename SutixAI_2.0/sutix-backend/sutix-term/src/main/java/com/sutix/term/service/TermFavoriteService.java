package com.sutix.term.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.term.entity.TermFavorite;
import java.util.List;

public interface TermFavoriteService extends IService<TermFavorite> {
    boolean addFavorite(Long userId, Long termId);
    boolean removeFavorite(Long userId, Long termId);
    List<Long> listFavoriteTermIds(Long userId);
    boolean isFavorite(Long userId, Long termId);
}