package com.sutix.term.dto;

import lombok.Data;

@Data
public class TermQueryDTO {
    /** 页码 */
    private Integer page = 1;
    /** 页大小 */
    private Integer size = 10;
    /** 搜索关键词（中文/俄文） */
    private String keyword;
    /** 分类名称（筛选） */
    private String categoryName;
}