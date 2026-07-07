package com.sutix.corpus.dto;

import com.sutix.corpus.entity.CorpusSentence;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentVO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private Long createUser;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CorpusSentence> sentences;
}
