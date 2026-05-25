package com.sutix.system.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 术语Excel导入导出模型
 */
@Data
public class TermExcelDTO {
    @ExcelProperty(value = "术语名称", index = 0) // 第0列，必填
    private String termName;

    @ExcelProperty(value = "术语释义", index = 1) // 第1列
    private String termExplain;

    @ExcelProperty(value = "分类名称", index = 2) // 第2列（导入时匹配分类名称）
    private String categoryName;
}