package com.sutix.common.utils;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * AI接口限流工具类（令牌桶算法）
 */
@Component
public class RateLimitUtil {

    @Resource
    private RedisUtil redisUtil;

    /**
     * 检查是否允许调用AI接口
     * @param userId 用户ID
     * @param maxCount 每分钟最大调用次数
     * @return true=允许，false=限流
     */
    public boolean allowCallAi(Long userId, int maxCount) {
        String key = "rate:limit:ai:" + userId;
        try {
            // 1. 获取当前调用次数
            Object countObj = redisUtil.get(key);
            int count = countObj == null ? 0 : Integer.parseInt(countObj.toString());
            // 2. 检查是否超过限制
            if (count >= maxCount) {
                return false;
            }
            // 3. 调用次数+1，设置过期时间1分钟
            redisUtil.set(key, count + 1, 60);
            return true;
        } catch (Exception e) {
            // 无Redis时不限流（生产环境建议关闭AI接口）
            System.out.println("限流检查失败（自动关闭限流）：" + e.getMessage());
            return true;
        }
    }
}