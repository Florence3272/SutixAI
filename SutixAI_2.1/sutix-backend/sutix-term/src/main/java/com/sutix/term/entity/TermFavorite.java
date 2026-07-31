package com.sutix.term.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("term_favorite")
public class TermFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long termId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}