package com.sutix.term.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TermFormDTO {
    /** 术语ID（编辑时传） */
    private Long id;
    /** 中文术语 */
    @NotBlank(message = "中文术语不能为空")
    private String name;
    /** 俄文术语 */
    @NotBlank(message = "俄文术语不能为空")
    private String ruName;
    /** 术语解释 */
    private String description;
    /** 分类ID */
    @NotNull(message = "分类不能为空")
    private Long categoryId;
}