package com.itheima.health.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.Arrays;

/**
 * mvc相关的配置
 *
 */
@Configuration
public class SpringMvcConfiguration {
    /**
     *  HTTP JSON消息转换器
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {

        //1.先定义一个转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        //2. 指定需要转换的数据类型（Content-Type）
        fastConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        //返回
        return new HttpMessageConverters(fastConverter);
    }
}