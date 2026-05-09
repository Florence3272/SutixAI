package com.sutix.system.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CategoryDTO {
    private Long id;                // 分类ID（修改/删除时用）
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;    // 分类名称
    private String categoryDesc;    // 分类描述
}