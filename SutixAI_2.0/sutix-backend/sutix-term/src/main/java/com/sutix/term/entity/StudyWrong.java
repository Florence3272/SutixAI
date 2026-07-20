package com.sutix.term.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_wrong")
public class StudyWrong {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long termId;
    private Integer wrongCount;
    private LocalDateTime lastWrongTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
