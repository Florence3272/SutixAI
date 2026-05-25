package com.sutix.common.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类（兼容无Redis环境，自动降级）
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存（带过期时间）
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            // 无Redis时忽略，自动降级为数据库查询
            System.out.println("Redis缓存设置失败（自动降级）：" + e.getMessage());
        }
    }

    /**
     * 获取缓存
     * @param key 缓存键
     * @return 缓存值（null表示无缓存）
     */
    public Object get(String key) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            return operations.get(key);
        } catch (Exception e) {
            // 无Redis时返回null，走数据库查询
            System.out.println("Redis缓存获取失败（自动降级）：" + e.getMessage());
            return null;
        }
    }

    /**
     * 删除缓存
     * @param key 缓存键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            System.out.println("Redis缓存删除失败（自动降级）：" + e.getMessage());
        }
    }
}