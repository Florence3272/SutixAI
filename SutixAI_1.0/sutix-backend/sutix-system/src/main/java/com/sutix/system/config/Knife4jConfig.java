package com.sutix.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j接口文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 文档基本信息
                .info(new Info()
                        .title("术语AI平台接口文档")
                        .version("1.0.0")
                        .description("术语AI平台的所有接口，包含用户、术语、分类、AI等模块")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")));
    }
}