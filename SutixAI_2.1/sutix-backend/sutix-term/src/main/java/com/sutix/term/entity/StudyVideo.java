package com.sutix.term.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_video")
public class StudyVideo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String url;
    private String poster;
    private Long categoryId;
    private Integer duration;
    private Long createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 关联术语ID列表（非数据库字段） */
    @TableField(exist = false)
    private java.util.List<Long> termIds;
}
