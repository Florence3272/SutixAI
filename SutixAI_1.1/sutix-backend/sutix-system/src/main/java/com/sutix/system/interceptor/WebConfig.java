package com.sutix.system.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 关键：不注册拦截器 = 彻底关闭登录校验
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 空的，什么都不写
    }
}