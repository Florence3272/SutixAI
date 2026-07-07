package com.sutix.corpus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("corpus_sentence")
public class CorpusSentence {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long documentId;
    private String zhText;
    private String ruText;
    private Integer seq;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
