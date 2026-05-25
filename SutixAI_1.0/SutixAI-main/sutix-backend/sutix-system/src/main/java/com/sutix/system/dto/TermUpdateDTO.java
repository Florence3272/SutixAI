package com.sutix.system.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class TermUpdateDTO {
    @NotNull(message = "术语ID不能为空")
    private Long id;                // 术语ID
    @NotBlank(message = "术语名称不能为空")
    private String termName;        // 术语名称
    private String termExplain;     // 术语释义
    private Long categoryId;        // 分类ID
}