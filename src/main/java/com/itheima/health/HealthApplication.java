package com.itheima.health;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.annotation.WebServlet;

@EnableScheduling   //开启定时任务注解功能
@Slf4j
@SpringBootApplication
@MapperScan("com.itheima.health.dao")
@EnableCaching
@ServletComponentScan
public class HealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
        log.info("Server started");
    }

}