package com.sutix.term.dto;

import lombok.Data;

@Data
public class CategoryCountVO {

    private Long categoryId;

    private String categoryName;

    private Long count;
}