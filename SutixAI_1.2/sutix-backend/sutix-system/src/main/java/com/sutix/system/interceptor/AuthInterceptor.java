package com.sutix.system.interceptor;

import com.sutix.system.entity.SysUser;
import com.sutix.system.service.SysUserService;
import com.sutix.common.utils.JwtUtil;
import com.sutix.system.utils.SecurityUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final SysUserService userService;

    public AuthInterceptor(JwtUtil jwtUtil, SysUserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录\"}");
            return false;
        }
        token = token.substring(7);
        if (!jwtUtil.verify(token)) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效\"}");
            return false;
        }
        Long userId = jwtUtil.getUserId(token);
        SysUser user = userService.getById(userId);
        if (user == null) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"用户不存在\"}");
            return false;
        }
        SecurityUtils.setUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityUtils.remove();
    }
}