package com.itheima.health.anno;

import com.itheima.health.common.CacheType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface L2Cache {
    String cacheName();
    String key(); //支持springEl表达式
    long l2TimeOut() default 120;
    CacheType type() default CacheType.FULL;
}
