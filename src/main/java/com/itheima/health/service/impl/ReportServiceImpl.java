package com.itheima.health.service.impl;

import com.itheima.health.dao.ReportDao;
import com.itheima.health.pojo.entity.MemberReport;
import com.itheima.health.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDao reportDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public MemberReport getMemberReport() {

        MemberReport memberReport = new MemberReport();
        List<String> months = redisTemplate.opsForList().range("months", 0, -1);
        List<Integer> memberCount = redisTemplate.opsForList().range("memberCount", 0, -1);
        memberReport.setMonths(months);
        memberReport.setMemberCount(memberCount);
        return memberReport;
    }


    @Scheduled(cron = "0 * * * * ?")
    public void getMemberReportTask() {
        log.info("定时查询会员数量");
        List<String> months = new ArrayList<>();;
        List<Integer> memberCount = new ArrayList<>();
        //获取上一年
        LocalDate lastYear = LocalDate.now().minusYears(1);
        for (int i = 0; i < 12; i++) {
            LocalDate localDate = lastYear.plusMonths(i);
            int i1 = localDate.lengthOfMonth();
            LocalDate startDay = LocalDate.now().minusYears(5);
            LocalDate endDay = localDate.withDayOfMonth(i1);
            if(i==11){
                endDay = LocalDate.now();
            }
            Integer count = reportDao.searchMemberNumberByMonth(startDay,endDay);
            months.add(localDate.toString().substring(0,7));
            memberCount.add(count);
        }
        redisTemplate.delete("months");
        redisTemplate.delete("memberCount");
        redisTemplate.opsForList().rightPushAll("months",months);
        redisTemplate.opsForList().rightPushAll("memberCount",memberCount);
    }

}
