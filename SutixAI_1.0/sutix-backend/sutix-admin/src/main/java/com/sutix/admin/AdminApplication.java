package com.sutix.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// 扫描所有模块的组件（common/system/term）
@ComponentScan(basePackages = {"com.sutix"})
// 扫描所有模块的Mapper接口（关键：指定正确的包路径）
@MapperScan(basePackages = {"com.sutix.*.mapper"})
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("===== Sutix术语AI平台后端启动成功 =====");
    }
}