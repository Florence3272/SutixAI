package com.sutix.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    // 令牌过期时间（7天）
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    // 密钥（自定义，建议至少32位）
    private static final String SECRET_KEY = "sutix-term-platform-2026-secret-key-1234567890";

    /**
     * 生成令牌
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public static String generateToken(Long userId, String username) {
        // 构建载荷
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        // 生成密钥
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        // 生成令牌
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * 解析令牌
     * @param token JWT令牌
     * @return 载荷信息
     */
    public static Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证令牌是否过期
     * @param token JWT令牌
     * @return true-过期，false-未过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }

    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 验证令牌有效性
     * @param token JWT令牌
     * @throws Exception 令牌无效或过期时抛出异常
     */
    public static void verifyToken(String token) throws Exception {
        if (isTokenExpired(token)) {
            throw new RuntimeException("令牌已过期");
        }
    }

    /**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }
}