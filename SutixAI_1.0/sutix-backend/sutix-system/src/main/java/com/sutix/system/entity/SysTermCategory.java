package com.sutix.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_term_category")
public class SysTermCategory {
    @TableId(type = IdType.AUTO)
    private Long id;                // 分类ID
    private String categoryName;    // 分类名称
    private String categoryDesc;    // 分类描述
    private Long createUserId;      // 创建人ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;        // 逻辑删除
}