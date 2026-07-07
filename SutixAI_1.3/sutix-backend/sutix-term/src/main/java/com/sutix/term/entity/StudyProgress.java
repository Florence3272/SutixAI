package com.sutix.term.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_progress")
public class StudyProgress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long termId;
    private String status;       // learning=学习中, mastered=已掌握, weak=未掌握, wrong=错题
    private Integer reviewCount;
    private LocalDateTime lastReviewTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
