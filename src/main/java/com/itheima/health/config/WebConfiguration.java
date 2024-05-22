package com.itheima.health.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置运行所有的请求跨域
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")// 针对所有的请求
            .allowedOrigins("*")    // 允许来自所有的域
            .allowedMethods("*")    // 允许所有的HTTP Method
            .allowCredentials(true);// 允许携带cookie
    }
}