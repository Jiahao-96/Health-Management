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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeleteFilesTask {
    @Autowired
    private RedisTemplate redisTemplate ;
    @Autowired
    private AliOssUtil aliOssUtil;

    @Scheduled(cron = "0 * * * * ?")
    public void processDeleteFiles() {
        log.info("调用了删除垃圾图片的定时方法捏");
        //获取redis中存的数据库图片和阿里云图片
        Set mysqlImgs = redisTemplate.opsForSet().members(RedisConst.MYSQL_PIC);
        Set aliyunImgs = redisTemplate.opsForSet().members(RedisConst.ALIYUN_PIC);
        if (aliyunImgs == null || mysqlImgs == null){
            return;
        }

        //把redis中的数据库图片和阿里云图片进行差值计算，得出阿里云中的垃圾图片集合
        Set<String> difference = redisTemplate.opsForSet().difference(RedisConst.ALIYUN_PIC, RedisConst.MYSQL_PIC);//顺序要注意，前面减后面
        List<String> rubbishImgs = difference.stream().collect(Collectors.toList());
        if (rubbishImgs == null || rubbishImgs.size() == 0){
            log.info("数据库和阿里云中的文件一致捏");
            return;
        }

        //如果图片上传时间小于30分钟，则不是垃圾图片，从集合删除。
        Iterator<String> i = rubbishImgs.iterator();
        while(i.hasNext()){//判断当前位置是否有元素
            String rubbishImg = i.next();//获取当前元素，并同时将迭代器对象移向下一个位置
            String substring = rubbishImg.substring(0, rubbishImg.indexOf("_"));
            Long createTime = Long.valueOf(substring);
            Long nowTime = System.currentTimeMillis();
            Long minute = (nowTime - createTime)/1000/60;
            if(minute < 30){
                i.remove();
                log.info("该图片才刚刚上传"+minute+"分钟，不能删：{}",rubbishImg);
            }
        }

        if (rubbishImgs == null || rubbishImgs.size() == 0){
            log.info("数据库和阿里云中的文件又一致了捏");
            return;
        }

        //删除阿里云垃圾图片，同时把redis中的阿里云垃圾图片也删掉。
        aliOssUtil.deleteFiles(rubbishImgs);
        Object[] array = rubbishImgs.toArray();
        redisTemplate.opsForSet().remove(RedisConst.ALIYUN_PIC,array);
        log.info("删除了这些垃圾照片捏：{}",rubbishImgs);


    }

}

