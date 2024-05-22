package com.itheima.health.task;

import com.itheima.health.common.RedisConst;
import com.itheima.health.service.SetMealService;
import com.itheima.health.util.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class CommonTask {
    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SetMealService setMealService;

    // 清理前最小等待时间 24h
    private static final Long MIN_DELETE_WAIT_TIME = 24 * 60 * 60 * 1000L;
    // private static final Long MIN_DELETE_WAIT_TIME = 60 * 1000L;    //测试用 60秒没有使用就清理

    /**
     * 定时清理一次阿里云的无效文件
     */
    @Scheduled(cron = "0 0 2 * * ?")            //每天凌晨2点触发
    // @Scheduled(cron = "0/10 * * * * ?")      //测试用，每隔10秒触发
    public void clearAliOssFile() {
        log.info("【清理垃圾图片】开始...");
        //清理垃圾图片
        //计算redis中两个集合的差值，获取垃圾图片名称
        Set<String> set = redisTemplate.boundSetOps(RedisConst.SETMEAL_PIC_RESOURCES).members();
        for (String img : set) {
            log.info("img = {}", img);
            Long createTime = Long.valueOf(img.split("_", 2)[0]);
            // 判断1:等待时间是否大于清理前的最小等待时间；防止用户表单未提交，图片就被删除了
            if (System.currentTimeMillis() - createTime < MIN_DELETE_WAIT_TIME) {
                log.info("【清理垃圾图片】等待时间小于清理前的最小等待时间，暂不清除:{}", img);
                continue;
            }

            // 判断2：判断是否被使用(操作数据库和操作redis不在一个事务内，所有有可能：表单已提交，redis中数据并未删除)
            long count = setMealService.countByImg(img);
            if (count == 0) {
                // 如果未被使用，则从阿里云删除
                log.info("【清理垃圾图片】{}", img);
                try {
                    aliOssUtil.deleteFile(img);
                } catch (RuntimeException e) {
                    log.error("【清理垃圾图片】失败:" + img, e);
                    continue;
                }
            } else {
                // 如果已经被使用，则无需从阿里云删除
                log.info("【清理垃圾图片】图片已使用，无需清理:{}", img);
            }
            // 无论是否垃圾图片，都从redis移除
            redisTemplate.boundSetOps(RedisConst.SETMEAL_PIC_RESOURCES).remove(img);
        }
        log.info("【清理垃圾图片】结束...");
    }
}
