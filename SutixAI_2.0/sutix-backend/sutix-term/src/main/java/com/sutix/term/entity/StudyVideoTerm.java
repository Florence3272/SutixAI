package com.sutix.term.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_video_term")
public class StudyVideoTerm {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long videoId;
    private Long termId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
