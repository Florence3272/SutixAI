package com.sutix.translation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sutix.translation.config.TransHomeConfig;
import com.sutix.translation.entity.TranslationRecord;
import com.sutix.translation.mapper.TranslationRecordMapper;
import com.sutix.translation.service.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class TranslationServiceImpl extends ServiceImpl<TranslationRecordMapper, TranslationRecord>
        implements TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationServiceImpl.class);

    @Resource
    private TranslationRecordMapper recordMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private TransHomeConfig transHomeConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<TranslationRecord> listUserRecords(Long userId) {
        QueryWrapper<TranslationRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public Long addRecord(TranslationRecord record) {
        save(record);
        return record.getId();
    }

    @Override
    public void updateResult(Long id, String resultText, String downloadUrl, Integer status) {
        TranslationRecord record = getById(id);
        if (record != null) {
            record.setResultText(resultText);
            record.setDownloadUrl(downloadUrl);
            record.setStatus(status);
            updateById(record);
        }
    }

    @Override
    public TranslationRecord translateText(String text, String direction, Long userId) {
        // 1. 将文本写入临时 .txt 文件
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("sutix_translate_", ".txt");
            Files.write(tempFile, text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("创建临时文件失败", e);
        }

        // 2. 确定目标语言代码（DeepL 语种代码）
        String targetLanguage = "RU"; // 默认中→俄
        if ("ru2zh".equals(direction)) {
            targetLanguage = "ZH";
        }

        // 3. 上传到 trans-home
        Long documentId;
        try {
            documentId = uploadToTransHome(tempFile.toFile(), targetLanguage);
        } catch (Exception e) {
            // 清理临时文件
            try { Files.deleteIfExists(tempFile); } catch (IOException ignored) {}
            throw new RuntimeException("上传翻译文件失败: " + e.getMessage(), e);
        }

        // 4. 记录文件名
        String fileName = "文本翻译_" + text.substring(0, Math.min(20, text.length())).replaceAll("[\\\\/:*?\"<>|]", "_") + ".txt";

        // 5. 存入数据库
        TranslationRecord record = new TranslationRecord();
        record.setUserId(userId);
        record.setDocumentId(documentId);
        record.setSourceText(text);
        record.setTargetLanguage(targetLanguage);
        record.setDirection(direction);
        record.setStatus(0); // 进行中
        record.setFileName(fileName);
        save(record);

        // 清理临时文件
        try { Files.deleteIfExists(tempFile); } catch (IOException ignored) {}

        log.info("文本翻译已提交, 记录ID={}, trans-home文档ID={}", record.getId(), documentId);
        return record;
    }

    @Override
    public TranslationRecord uploadDocument(MultipartFile file, String direction, Long userId) {
        // 1. 确定目标语言
        String targetLanguage = "RU";
        if ("ru2zh".equals(direction)) {
            targetLanguage = "ZH";
        }

        // 2. 上传到 trans-home (使用 MultipartFile 直接上传)
        Long documentId;
        try {
            // 将 MultipartFile 保存为临时文件后上传
            Path tempFile = Files.createTempFile("sutix_doc_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            try {
                documentId = uploadToTransHome(tempFile.toFile(), targetLanguage);
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } catch (Exception e) {
            throw new RuntimeException("上传文档失败: " + e.getMessage(), e);
        }

        // 3. 存入数据库
        TranslationRecord record = new TranslationRecord();
        record.setUserId(userId);
        record.setDocumentId(documentId);
        record.setTargetLanguage(targetLanguage);
        record.setDirection(direction);
        record.setStatus(0);
        record.setFileName(file.getOriginalFilename());

        // 尝试读取原文内容（仅文本类文件）
        try {
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                String lower = fileName.toLowerCase();
                if (lower.endsWith(".txt")) {
                    record.setSourceText(new String(file.getBytes(), StandardCharsets.UTF_8));
                }
            }
        } catch (Exception e) {
            log.warn("读取原文内容失败(不影响翻译): {}", e.getMessage());
        }

        save(record);

        log.info("文档翻译已提交, 记录ID={}, trans-home文档ID={}, 文件名={}",
                record.getId(), documentId, file.getOriginalFilename());
        return record;
    }

    @Override
    public Map<String, Object> getProgress(Long id) {
        TranslationRecord record = getById(id);
        if (record == null) {
            throw new RuntimeException("翻译记录不存在: " + id);
        }

        // 如果本地已经完成，直接返回
        if (record.getStatus() == 1 || record.getStatus() == -1) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", record.getStatus());
            result.put("resultText", record.getResultText());
            result.put("downloadUrl", record.getDownloadUrl());
            return result;
        }

        // 查询 trans-home 进度
        try {
            String url = transHomeConfig.getProgressUrl(record.getDocumentId());
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();

            log.debug("trans-home 进度响应: {}", body);

            if (body == null) {
                return buildProgressResult(0, null, null);
            }

            JsonNode root = objectMapper.readTree(body);
            int code = root.get("code").asInt();

            if (code != 1) {
                String info = root.has("info") ? root.get("info").asText() : "查询失败";
                log.warn("trans-home 查询进度失败: {}", info);
                return buildProgressResult(0, null, null);
            }

            JsonNode data = root.get("data");
            if (data == null) {
                return buildProgressResult(0, null, null);
            }

            int transStatus = data.has("status") ? data.get("status").asInt() : 0;
            String downloadUrl = data.has("download_url") ? data.get("download_url").asText() : null;

            if (transStatus == 1) {
                // 翻译成功 → 下载结果并提取文本
                String resultText = downloadAndExtractText(downloadUrl);

                // 更新数据库
                record.setStatus(1);
                record.setResultText(resultText);
                record.setDownloadUrl(downloadUrl);
                updateById(record);

                log.info("翻译完成, 记录ID={}, downloadUrl={}", id, downloadUrl);
                return buildProgressResult(1, resultText, downloadUrl);

            } else if (transStatus == -1) {
                // 翻译失败
                record.setStatus(-1);
                updateById(record);
                log.warn("翻译失败, 记录ID={}", id);
                return buildProgressResult(-1, null, null);

            } else {
                // 翻译中
                return buildProgressResult(0, null, null);
            }

        } catch (Exception e) {
            log.error("查询翻译进度失败, 记录ID={}: {}", id, e.getMessage());
            return buildProgressResult(0, null, null);
        }
    }

    // ===================== 私有辅助方法 =====================

    /**
     * 上传文件到 trans-home API
     */
    private Long uploadToTransHome(File file, String targetLanguage) throws Exception {
        String url = transHomeConfig.getUploadUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("targetLanguage", targetLanguage);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class);

        String responseBody = response.getBody();
        log.debug("trans-home 上传响应: {}", responseBody);

        if (responseBody == null) {
            throw new RuntimeException("上传响应为空");
        }

        JsonNode root = objectMapper.readTree(responseBody);
        int code = root.get("code").asInt();

        if (code != 1) {
            String info = root.has("info") ? root.get("info").asText() : "未知错误";
            throw new RuntimeException("上传失败: " + info);
        }

        JsonNode data = root.get("data");
        if (data == null || !data.has("document_id")) {
            throw new RuntimeException("上传响应缺少 document_id");
        }

        return data.get("document_id").asLong();
    }

    /**
     * 从 trans-home 返回的下载链接下载并提取文本内容
     * 结果文件是 .html 格式，需要提取其中的纯文本
     */
    private String downloadAndExtractText(String downloadUrl) {
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            return null;
        }

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(downloadUrl, String.class);
            String html = response.getBody();
            if (html == null) return null;

            // 提取 HTML 中的纯文本（去除标签）
            String text = html
                    .replaceAll("(?is)<head>.*?</head>", "")
                    .replaceAll("(?is)<script[^>]*>.*?</script>", "")
                    .replaceAll("(?is)<style[^>]*>.*?</style>", "")
                    .replaceAll("<[^>]+>", " ")
                    .replaceAll("&nbsp;", " ")
                    .replaceAll("&lt;", "<")
                    .replaceAll("&gt;", ">")
                    .replaceAll("&amp;", "&")
                    .replaceAll("\\s+", " ")
                    .replaceAll("\\n\\s*\\n", "\n")
                    .trim();

            return text;
        } catch (Exception e) {
            log.error("下载翻译结果失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 构建进度查询返回结果
     */
    private Map<String, Object> buildProgressResult(int status, String resultText, String downloadUrl) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("resultText", resultText);
        result.put("downloadUrl", downloadUrl);
        return result;
    }
}
