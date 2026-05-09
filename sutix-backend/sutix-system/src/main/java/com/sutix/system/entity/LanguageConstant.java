package com.sutix.system.entity;

/**
 * DeepLX 语言编码常量类
 * 对应 DeepLX API 支持的语言编码，统一管理避免硬编码
 * 编码规则：遵循 DeepLX 官方规范，小写双字符（部分特殊语言为多字符）
 */
public final class LanguageConstant {

    // ===================== 私有化构造方法（工具类禁止实例化）=====================
    private LanguageConstant() {
        throw new UnsupportedOperationException("LanguageConstant 是常量类，禁止实例化");
    }

    // ===================== 常用语言（优先推荐）=====================
    /** 中文（简体） */
    public static final String CHINESE_SIMPLIFIED = "zh";
    /** 英语（通用） */
    public static final String ENGLISH = "en";
    /** 日语 */
    public static final String JAPANESE = "ja";
    /** 韩语 */
    public static final String KOREAN = "ko";
    /** 法语 */
    public static final String FRENCH = "fr";
    /** 德语 */
    public static final String GERMAN = "de";
    /** 西班牙语 */
    public static final String SPANISH = "es";
    /** 葡萄牙语 */
    public static final String PORTUGUESE = "pt";
    /** 意大利语 */
    public static final String ITALIAN = "it";
    /** 俄语 */
    public static final String RUSSIAN = "ru";

    // ===================== 其他支持的语言（扩展用）=====================
    /** 中文（繁体） */
    public static final String CHINESE_TRADITIONAL = "zh-TW";
    /** 英语（美式） */
    public static final String ENGLISH_US = "en-US";
    /** 英语（英式） */
    public static final String ENGLISH_GB = "en-GB";
    /** 荷兰语 */
    public static final String DUTCH = "nl";
    /** 波兰语 */
    public static final String POLISH = "pl";
    /** 瑞典语 */
    public static final String SWEDISH = "sv";
    /** 丹麦语 */
    public static final String DANISH = "da";
    /** 芬兰语 */
    public static final String FINNISH = "fi";
    /** 挪威语 */
    public static final String NORWEGIAN = "no";
    /** 捷克语 */
    public static final String CZECH = "cs";
    /** 匈牙利语 */
    public static final String HUNGARIAN = "hu";
    /** 罗马尼亚语 */
    public static final String ROMANIAN = "ro";

    // ===================== 辅助方法（可选，增强易用性）=====================
    /**
     * 验证语言编码是否为 DeepLX 支持的编码
     * @param langCode 待验证的语言编码
     * @return true-支持，false-不支持
     */
    public static boolean isSupportedLanguage(String langCode) {
        if (langCode == null || langCode.trim().isEmpty()) {
            return false;
        }
        String code = langCode.trim();
        return code.equals(CHINESE_SIMPLIFIED)
                || code.equals(ENGLISH)
                || code.equals(JAPANESE)
                || code.equals(KOREAN)
                || code.equals(FRENCH)
                || code.equals(GERMAN)
                || code.equals(SPANISH)
                || code.equals(PORTUGUESE)
                || code.equals(ITALIAN)
                || code.equals(RUSSIAN)
                || code.equals(CHINESE_TRADITIONAL)
                || code.equals(ENGLISH_US)
                || code.equals(ENGLISH_GB)
                || code.equals(DUTCH)
                || code.equals(POLISH)
                || code.equals(SWEDISH)
                || code.equals(DANISH)
                || code.equals(FINNISH)
                || code.equals(NORWEGIAN)
                || code.equals(CZECH)
                || code.equals(HUNGARIAN)
                || code.equals(ROMANIAN);
    }

    /**
     * 获取语言编码对应的中文名称
     * @param langCode 语言编码
     * @return 中文名称，未知编码返回"未知语言"
     */
    public static String getLanguageName(String langCode) {
        if (langCode == null) {
            return "未知语言";
        }
        return switch (langCode.trim()) {
            case CHINESE_SIMPLIFIED -> "中文（简体）";
            case CHINESE_TRADITIONAL -> "中文（繁体）";
            case ENGLISH -> "英语";
            case ENGLISH_US -> "英语（美式）";
            case ENGLISH_GB -> "英语（英式）";
            case JAPANESE -> "日语";
            case KOREAN -> "韩语";
            case FRENCH -> "法语";
            case GERMAN -> "德语";
            case SPANISH -> "西班牙语";
            case PORTUGUESE -> "葡萄牙语";
            case ITALIAN -> "意大利语";
            case RUSSIAN -> "俄语";
            case DUTCH -> "荷兰语";
            case POLISH -> "波兰语";
            case SWEDISH -> "瑞典语";
            case DANISH -> "丹麦语";
            case FINNISH -> "芬兰语";
            case NORWEGIAN -> "挪威语";
            case CZECH -> "捷克语";
            case HUNGARIAN -> "匈牙利语";
            case ROMANIAN -> "罗马尼亚语";
            default -> "未知语言";
        };
    }
}