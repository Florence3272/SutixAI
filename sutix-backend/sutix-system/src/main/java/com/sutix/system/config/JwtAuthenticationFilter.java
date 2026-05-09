package com.sutix.system.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.sutix.common.utils.JwtUtil;
import com.sutix.common.result.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT权限校验拦截器
 * 继承OncePerRequestFilter：确保每个请求只被过滤一次
 */
@Component  // 关键注解：让Spring扫描并管理这个类
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 如果你的JwtUtil是通过@Bean创建的，需要注入；如果是静态工具类，可省略
    @Autowired(required = false)
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 1. 获取请求URL，排除登录/注册接口（不需要token）
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/user/login") || requestURI.contains("/user/register")) {
            chain.doFilter(request, response);  // 放行
            return;
        }

        // 2. 从请求头获取token
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            // token为空，返回401未登录
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONUtil.toJsonStr(Result.error(401, "未登录，请先登录")));
            return;
        }

        // 3. 校验token有效性（调用JwtUtil的验证方法）
        try {
            JwtUtil.verifyToken(token);  // 如果是静态方法，直接调用；如果是实例方法，用this.jwtUtil.verifyToken(token)
        } catch (Exception e) {
            // token无效/过期，返回401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONUtil.toJsonStr(Result.error(401, "token无效或已过期：" + e.getMessage())));
            return;
        }

        // 4. token有效，放行请求
        chain.doFilter(request, response);
    }
}