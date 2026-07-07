package com.sutix.translation.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileParseService {
    /** 从文件中提取文本内容 */
    String parseFile(MultipartFile file) throws Exception;
}
