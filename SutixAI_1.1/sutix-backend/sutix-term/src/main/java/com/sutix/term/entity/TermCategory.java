package com.sutix.term.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("term_category")
public class TermCategory {
    /** 分类ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 分类名称 */
    private String categoryName;
    /** 创建人id */
    private Long createUser;
    /** 创建人名称 */
    @TableField(exist = false)
    private String createUserName;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}