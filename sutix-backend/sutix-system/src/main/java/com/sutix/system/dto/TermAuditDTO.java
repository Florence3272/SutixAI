package com.sutix.system.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class TermAuditDTO {
    @NotNull(message = "术语ID不能为空")
    private Long termId;            // 术语ID
    @NotNull(message = "审核状态不能为空（1=通过，2=驳回）")
    private Integer status;         // 审核状态
    private String rejectReason;    // 驳回原因（status=2时必填）
}