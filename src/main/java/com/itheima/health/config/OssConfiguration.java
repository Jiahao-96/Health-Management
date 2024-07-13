package com.itheima.health.config;

import com.itheima.health.properties.AliOssProperties;
import com.itheima.health.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里oss配置类，用来生成AliOssUtil对象，并交由IOC容器管理
 */
@Slf4j
@Configuration
public class OssConfiguration {
    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("初始化aliOssUtil对象");
        return new AliOssUtil(aliOssProperties.getUrlPrefix(),aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());
    }
}
