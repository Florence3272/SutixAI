package com.sutix.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * DeepLX 翻译工具类
 * 遵循 DeepLX API 规范，提供简洁、健壮的翻译接口
 */
public class AiUtil {
    // ===================== 可配置参数（建议抽离到配置文件）=====================
    /** DeepLX API 地址（本地部署/反向代理地址） */
    private static final String DEEPLX_API_URL = "http://localhost:1188/translate";
    /** 连接超时时间（秒） */
    private static final int CONNECT_TIMEOUT = 10;
    /** 读取超时时间（秒） */
    private static final int READ_TIMEOUT = 20;

    // ===================== 常量定义 =====================
    /** 成功状态码 */
    private static final int SUCCESS_CODE = 200;
    /** JSON 响应体中的数据字段 */
    private static final String DATA_FIELD = "data";
    /** JSON 响应体中的错误字段 */
    private static final String ERROR_FIELD = "error";
    /** Content-Type 请求头 */
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    // ===================== 静态工具实例 =====================
    /** HTTP 客户端（复用，避免频繁创建） */
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT))
            .build();
    /** JSON 解析器（线程安全） */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // ===================== 私有化构造方法（工具类禁止实例化）=====================
    private AiUtil() {
        throw new UnsupportedOperationException("AiUtil 是工具类，禁止实例化");
    }

    // ===================== 核心翻译方法（静态）=====================
    /**
     * 通用翻译方法（自动检测源语言）
     * @param text 待翻译文本（不能为空）
     * @param targetLang 目标语言（如：zh, en, ja, ko, fr, de 等）
     * @return 翻译结果
     * @throws DeepLXException 翻译异常（包含具体错误信息）
     */
    public static String translate(String text, String targetLang) throws DeepLXException {
        return translate(text, "", targetLang);
    }

    /**
     * 通用翻译方法（指定源语言）
     * @param text 待翻译文本（不能为空）
     * @param sourceLang 源语言（如：zh, en；空字符串表示自动检测）
     * @param targetLang 目标语言（如：zh, en, ja, ko, fr, de 等，不能为空）
     * @return 翻译结果
     * @throws DeepLXException 翻译异常（包含具体错误信息）
     */
    public static String translate(String text, String sourceLang, String targetLang) throws DeepLXException {
        // 1. 参数校验
        validateParams(text, targetLang);

        // 2. 构造请求参数
        Map<String, String> requestParams = buildRequestParams(text, sourceLang, targetLang);

        // 3. 发送 HTTP 请求并获取响应
        String responseBody = sendPostRequest(requestParams);

        // 4. 解析响应结果
        return parseResponse(responseBody);
    }

    // ===================== 私有辅助方法（全部改为静态）=====================
    /**
     * 参数校验
     * @param text 待翻译文本
     * @param targetLang 目标语言
     * @throws DeepLXException 参数不合法时抛出异常
     */
    private static void validateParams(String text, String targetLang) throws DeepLXException {
        if (text == null || text.trim().isEmpty()) {
            throw new DeepLXException("待翻译文本不能为空");
        }
        if (targetLang == null || targetLang.trim().isEmpty()) {
            throw new DeepLXException("目标语言不能为空（如：zh, en, ja）");
        }
    }

    /**
     * 构造请求参数
     * @param text 待翻译文本
     * @param sourceLang 源语言
     * @param targetLang 目标语言
     * @return 请求参数字典
     */
    private static Map<String, String> buildRequestParams(String text, String sourceLang, String targetLang) {
        Map<String, String> params = new HashMap<>(3);
        params.put("text", text.trim());
        params.put("source_lang", sourceLang == null ? "" : sourceLang.trim());
        params.put("target_lang", targetLang.trim());
        return params;
    }

    /**
     * 发送 POST 请求到 DeepLX API
     * @param requestParams 请求参数
     * @return 响应体字符串
     * @throws DeepLXException 请求失败时抛出异常
     */
    private static String sendPostRequest(Map<String, String> requestParams) throws DeepLXException {
        try {
            // 序列化请求参数为 JSON
            String requestBody = OBJECT_MAPPER.writeValueAsString(requestParams);

            // 构建 HTTP 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(DEEPLX_API_URL))
                    .header("Content-Type", CONTENT_TYPE_JSON)
                    .timeout(Duration.ofSeconds(READ_TIMEOUT))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            // 发送请求并获取响应
            HttpResponse<String> response = HTTP_CLIENT.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            // 校验响应状态码
            if (response.statusCode() != SUCCESS_CODE) {
                throw new DeepLXException(
                        String.format("DeepLX API 请求失败，状态码：%d，响应内容：%s",
                                response.statusCode(),
                                response.body())
                );
            }

            return response.body();
        } catch (JsonProcessingException e) {
            throw new DeepLXException("JSON 序列化失败：" + e.getMessage(), e);
        } catch (Exception e) {
            throw new DeepLXException("HTTP 请求失败：" + e.getMessage(), e);
        }
    }

    /**
     * 解析 DeepLX API 响应
     * @param responseBody 响应体字符串
     * @return 翻译结果
     * @throws DeepLXException 解析失败或接口返回错误时抛出异常
     */
    private static String parseResponse(String responseBody) throws DeepLXException {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(responseBody);

            // 检查是否包含错误信息
            if (jsonNode.has(ERROR_FIELD) && !jsonNode.get(ERROR_FIELD).isNull()) {
                throw new DeepLXException("DeepLX API 返回错误：" + jsonNode.get(ERROR_FIELD).asText());
            }

            // 检查是否包含数据字段
            if (!jsonNode.has(DATA_FIELD) || jsonNode.get(DATA_FIELD).isNull()) {
                throw new DeepLXException("DeepLX API 响应格式异常，缺少 data 字段：" + responseBody);
            }

            return jsonNode.get(DATA_FIELD).asText();
        } catch (JsonProcessingException e) {
            throw new DeepLXException("JSON 解析失败：" + e.getMessage(), e);
        }
    }

    // ===================== 自定义异常类 =====================
    /**
     * DeepLX 翻译异常（封装所有翻译相关错误）
     */
    public static class DeepLXException extends Exception {
        public DeepLXException(String message) {
            super(message);
        }

        public DeepLXException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ===================== 测试方法（可选）=====================
    public static void main(String[] args) {
        try {
            // 测试1：自动检测源语言，中文转英文
            String result1 = AiUtil.translate("你好，世界！", "en");
            System.out.println("翻译结果1：" + result1); // 输出：Hello, world!

            // 测试2：指定源语言，英文转中文
            String result2 = AiUtil.translate("Java is a great programming language", "en", "zh");
            System.out.println("翻译结果2：" + result2); // 输出：Java 是一门很棒的编程语言

            // 测试3：中文转日语
            String result3 = AiUtil.translate("编程改变世界", "zh", "ja");
            System.out.println("翻译结果3：" + result3); // 输出：プログラミングは世界を変える
        } catch (DeepLXException e) {
            System.err.println("翻译失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}