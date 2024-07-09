package com.itheima.health.task;


import com.itheima.health.common.RedisConst;
import com.itheima.health.dao.SetMealDao;
import com.itheima.health.pojo.entity.Setmeal;
import com.itheima.health.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeleteFilesTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AliOssUtil aliOssUtil;

    @Scheduled(cron = "0 * * * * ?")
    public void processDeleteFiles() {
        log.info("调用了定时方法捏");

        //获取redis中存的数据库图片和阿里云图片
        Set mysqlImgs = redisTemplate.opsForSet().members(RedisConst.MYSQL_PIC);
        Set aliyunImgs = redisTemplate.opsForSet().members(RedisConst.ALIYUN_PIC);
        if (aliyunImgs == null || mysqlImgs == null){
            return;
        }
        //把redis中的数据库图片和阿里云图片进行差值计算，得出阿里云中的垃圾图片集合
        Set<String> difference = redisTemplate.opsForSet().difference(RedisConst.ALIYUN_PIC, RedisConst.MYSQL_PIC);//顺序要注意，前面减后面
        List<String> rubbishImg = difference.stream().collect(Collectors.toList());
        if (rubbishImg == null || rubbishImg.size() == 0){
            log.info("数据库和阿里云中的文件一致捏");
            return;
        }
        //删除阿里云垃圾图片，同时把redis中的阿里云垃圾图片也删掉。
        aliOssUtil.deleteFiles(rubbishImg);
        Object[] array = rubbishImg.toArray();
        redisTemplate.opsForSet().remove(RedisConst.ALIYUN_PIC,array);
        log.info("删除了这些垃圾照片捏：{}",rubbishImg);

    }
}

