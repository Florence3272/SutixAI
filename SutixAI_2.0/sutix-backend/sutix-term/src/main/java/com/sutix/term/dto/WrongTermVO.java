package com.sutix.term.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WrongTermVO {
    private Long id;           // 错题记录ID
    private Long termId;       // 术语ID
    private Long userId;
    private String name;       // 中文术语
    private String enName;     // 英文术语
    private String ruName;     // 俄文术语
    private String description;
    private Long categoryId;
    private String categoryName;
    private Integer wrongCount;
    private LocalDateTime lastWrongTime;
    private LocalDateTime createTime;
}
