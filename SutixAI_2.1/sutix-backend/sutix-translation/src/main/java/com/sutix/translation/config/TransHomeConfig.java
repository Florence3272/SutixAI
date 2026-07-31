package com.sutix.translation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * trans-home 文档翻译 API 配置
 * 对应 application-dev.yml 中的 trans-home 节点
 */
@Data
@Component
@ConfigurationProperties(prefix = "trans-home")
public class TransHomeConfig {

    /** API 主机地址 */
    private String host;

    /** API 鉴权 Token */
    private String token;

    /** 连接超时 (ms) */
    private int connectTimeout = 15000;

    /** 读取超时 (ms) */
    private int readTimeout = 60000;

    /** 获取完整的上传 URL */
    public String getUploadUrl() {
        return host + "/api/document/upload?token=" + token;
    }

    /** 获取 URL 上传 URL */
    public String getUrlUploadUrl() {
        return host + "/api/document/urlUpload?token=" + token;
    }

    /** 获取进度查询 URL */
    public String getProgressUrl(Long documentId) {
        return host + "/api/document/progress?token=" + token + "&document_id=" + documentId;
    }

    /** 获取记录列表 URL */
    public String getRecordUrl() {
        return host + "/api/document/record?token=" + token;
    }
}
