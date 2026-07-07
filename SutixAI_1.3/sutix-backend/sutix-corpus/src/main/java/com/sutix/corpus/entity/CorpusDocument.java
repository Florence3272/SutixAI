package com.sutix.corpus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("corpus_document")
public class CorpusDocument {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private Long createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
