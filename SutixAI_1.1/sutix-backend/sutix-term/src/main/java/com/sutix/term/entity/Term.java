package com.sutix.term.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("term")
public class Term {
    /** 术语ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 中文术语 */
    private String name;
    /** 俄文术语 */
    private String ruName;
    /** 术语解释 */
    private String description;
    /** 分类ID */
    private Long categoryId;
    /** 创建人ID */
    private Long createUser;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 分类名称（非数据库字段，联表查询用） */
    @TableField(exist = false)
    private String categoryName;
    /** 创建人名称（非数据库字段，联表查询用） */
    @TableField(exist = false)
    private String createUserName;
}