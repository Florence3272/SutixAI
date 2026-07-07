package com.sutix.common.config;

import com.sutix.common.utils.JwtUtil;
// 关键：全部改成 javax.servlet
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    // 白名单路径：必须包含带/api和不带/api的所有登录相关路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/user/login", "/api/user/register", "/api/user/checkusername",
            "/user/login", "/user/register", "/user/checkusername"
    );

    // 关键：这个方法必须重写，用于判断是否需要跳过拦截器
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 1. 放行所有OPTIONS跨域预检请求（必须）
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 2. 放行白名单里的所有路径
        return WHITE_LIST.stream().anyMatch(uri -> requestURI.equals(uri));
    }


    // 方法签名必须和父类完全一致（javax.servlet 版本）
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 无 token 直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Long userId;

        try {
            userId = jwtUtil.getUserId(token); // 替换成你 JwtUtil 里的真实方法名
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (userId == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}