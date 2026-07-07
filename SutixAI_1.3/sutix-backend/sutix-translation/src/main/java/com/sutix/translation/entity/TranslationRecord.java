package com.sutix.translation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("translation_record")
public class TranslationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long documentId;
    private String sourceText;
    private String resultText;
    private String targetLanguage;
    private String direction;
    private Integer status;        // -1=失败 0=进行中 1=成功
    private String downloadUrl;
    private Integer billedChars;
    private String fileName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
