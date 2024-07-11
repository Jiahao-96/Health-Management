package com.itheima.health.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.health.dao.*;
import com.itheima.health.pojo.entity.*;
import com.itheima.health.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDao reportDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrdersettingDao ordersettingDao;
    @Autowired
    private MemberDao memberDao;

    @Override
    public MemberReport getMemberReport() {

        MemberReport memberReport = new MemberReport();
        List<String> months = redisTemplate.opsForList().range("months", 0, -1);
        List<Integer> memberCount = redisTemplate.opsForList().range("memberCount", 0, -1);
        memberReport.setMonths(months);
        memberReport.setMemberCount(memberCount);
        return memberReport;
    }

    @Override
    public SetmealReport getSetmealReport() {
        List<String> setmealNames = reportDao.searchSetmealName();
        List<SetmealNameValue> setmealCount = reportDao.searchSetmealNameAndOrderNumber();
        SetmealReport setmealReport = new SetmealReport();
        setmealReport.setSetmealNames(setmealNames);
        setmealReport.setSetmealCount(setmealCount);
        return setmealReport;
    }

    @Override
    public BusinessReport getBusinessReportData() {
        LocalDate nowDay = LocalDate.now();
        LocalDate weekStartDay = nowDay.minusDays(nowDay.getDayOfWeek().getValue()-1);
        LocalDate monthStartDay = nowDay.minusDays(Integer.valueOf(Integer.valueOf(nowDay.getDayOfMonth()))-1);

        //日、周、月 预约人数
        Integer reservationsDay = ordersettingDao.searchOrdersettingNumberByDay(nowDay);
        reservationsDay = reservationsDay == null ? 0 : reservationsDay;

        Integer reservationsWeek = ordersettingDao.searchOrdersettingNumberByWeek(weekStartDay, nowDay);
        reservationsWeek = reservationsWeek == null ? 0 : reservationsWeek;

        Integer reservationsMonth = ordersettingDao.searchOrdersettingNumberByMonth(monthStartDay, nowDay);
        reservationsMonth = reservationsMonth == null ? 0 : reservationsMonth;

        //热门套餐名称和数量
        List<HotSetmealInfo> hotSetmealOld = reportDao.searchHotSetmealNameAndOrderNumber();

        //套餐总量
        Integer allSetmealNumber = reportDao.searchAllSetmealNumber();

        //计算套餐占比
        List<HotSetmealInfo> hotSetmeal = hotSetmealOld.stream().map(hotSetmealInfo -> {
            //精度浮点数，四舍五入
            hotSetmealInfo.setProportion(BigDecimal.valueOf((1.0 * hotSetmealInfo.getSetmeal_count() / allSetmealNumber)).setScale(3, RoundingMode.HALF_UP));
            return hotSetmealInfo;
        }).collect(Collectors.toList());

        //总会员数
        Integer totalMember = memberDao.searchMemberNumberByDayOrAll(null);


        //日、月、周就诊人数和会员数直接封装
        BusinessReport businessReport = BusinessReport.builder().
                todayVisitsNumber(orderDao.searchVisitsNumberByDay(nowDay)).
                thisWeekVisitsNumber(orderDao.searchVisitsNumberByWeek(weekStartDay,nowDay)).
                thisMonthVisitsNumber(orderDao.searchVisitsNumberByMonth(monthStartDay,nowDay)).
                todayNewMember(memberDao.searchMemberNumberByDayOrAll(nowDay)).
                thisWeekNewMember(memberDao.searchMemberNumberByWeek(weekStartDay,nowDay)).
                thisMonthNewMember(memberDao.searchMemberNumberByMonth(monthStartDay,nowDay)).
                todayOrderNumber(reservationsDay).
                thisWeekOrderNumber(reservationsWeek).
                thisMonthOrderNumber(reservationsMonth).
                totalMember(totalMember).
                hotSetmeal(hotSetmeal).
                reportDate(nowDay).build();
        return businessReport;
    }


    @Scheduled(cron = "0 0 0 * * ?")
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
