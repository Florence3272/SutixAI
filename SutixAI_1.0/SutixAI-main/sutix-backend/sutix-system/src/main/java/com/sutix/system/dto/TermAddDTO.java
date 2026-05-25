package com.sutix.system.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TermAddDTO {
    @NotBlank(message = "术语名称不能为空")
    private String termName;        // 术语名称
    private String termExplain;     // 术语释义
    private Long categoryId = 0L;   // 分类ID（默认0）
}