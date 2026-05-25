package com.sutix.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_term")
public class SysTerm {
    @TableId(type = IdType.AUTO)
    private Long id;                // 术语ID
    private String termName;        // 术语名称
    private String termExplain;     // 术语释义
    private Long categoryId;        // 分类ID
    private Long createUserId;      // 创建人ID
    private Integer status;         // 审核状态：0=待审核，1=通过，2=驳回
    private String rejectReason;    // 驳回原因
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
    @TableLogic
    private Integer deleted;        // 逻辑删除
}