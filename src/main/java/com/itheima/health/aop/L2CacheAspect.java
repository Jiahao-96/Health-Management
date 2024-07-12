package com.itheima.health.aop;

import com.github.benmanes.caffeine.cache.Cache;
import com.itheima.health.anno.L2Cache;
import com.itheima.health.common.CacheType;
import com.itheima.health.utils.ElParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Aspect
@AllArgsConstructor
public class L2CacheAspect {

    @Autowired
    @Qualifier("customRedisTemplate")
    private final RedisTemplate customRedisTemplate;


    private final Cache cache;

    @Pointcut("@annotation(com.itheima.health.anno.L2Cache)")
    public void cacheAspect() {
    }

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        //拼接解析springEl表达式的map
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            treeMap.put(paramNames[i],args[i]);
        }

        L2Cache annotation = method.getAnnotation(L2Cache.class);
        String elResult = ElParser.parse(annotation.key(), treeMap);
        String realKey = annotation.cacheName() + ":" + elResult + ":";
        //强制更新
        if (annotation.type()== CacheType.PUT){
            Object object = point.proceed();
            customRedisTemplate.opsForValue().set(realKey, object,annotation.l2TimeOut(), TimeUnit.SECONDS);
            cache.put(realKey, object);
            return object;
        }
        //删除
        else if (annotation.type()== CacheType.DELETE){
            log.info("正在删除redis和caffeine的数据");
            Set<String> keys = scanAllKeys(customRedisTemplate);
            for(String key : keys){
                System.out.println(key);
                if(key.contains(annotation.cacheName())){
                    System.out.println(key);
                    customRedisTemplate.delete(key);
                }
            }
            //删除caffeine中缓存
            cache.invalidateAll();
            return point.proceed();
        }

        //读写，查询Caffeine
        Object caffeineCache = cache.getIfPresent(realKey);
        if (Objects.nonNull(caffeineCache)) {
            log.info("get data from caffeine");
            return caffeineCache;
        }

        //查询Redis
        Object redisCache = customRedisTemplate.opsForValue().get(realKey);
        if (Objects.nonNull(redisCache)) {
            log.info("get data from redis");
            cache.put(realKey, redisCache);
            return redisCache;
        }

        log.info("get data from database");
        Object object = point.proceed();
        if (Objects.nonNull(object)){
            //写入Redis
            customRedisTemplate.opsForValue().set(realKey, object,annotation.l2TimeOut(), TimeUnit.MINUTES);
            //写入Caffeine
            cache.put(realKey, object);
        }
        return object;
    }

    public Set<String> scanAllKeys(RedisTemplate<String, String> redisTemplate) {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match("*").count(100).build();
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);
        while (cursor.hasNext()) {
            byte[] next = cursor.next();
            keys.add(new String(next));
        }
        return keys;
    }


}