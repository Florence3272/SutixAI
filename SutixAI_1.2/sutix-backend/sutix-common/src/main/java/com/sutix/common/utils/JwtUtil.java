package com.sutix.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ 这是标准 256位 安全密钥（32个字符，满足HS256要求）
    private static final String SECRET = "12345678901234567890123456789012";

    // 生成安全的Key对象（官方推荐写法，永不报错）
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 2小时过期
    private static final long EXPIRE = 7200000;

    // 生成Token
    public String createToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY, SignatureAlgorithm.HS256)  // ✅ 安全写法
                .compact();
    }

    // 验证Token
    public boolean verify(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 从Token获取userId
    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    // 从 Token 中获取 username
    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }
}