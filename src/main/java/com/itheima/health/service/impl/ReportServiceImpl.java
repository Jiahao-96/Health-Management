package com.itheima.health.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.health.dao.*;
import com.itheima.health.pojo.entity.*;
import com.itheima.health.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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

        //总会员数
        Integer totalMember = memberDao.searchMemberNumberByDayOrAll(null);

        //热门套餐名称和数量
        List<HotSetmealInfo> hotSetmealOld = reportDao.searchHotSetmealNameAndOrderNumber();
        List<HotSetmealInfo> hotSetmeal = null;
        if (hotSetmealOld != null && hotSetmealOld.size() > 0){
            //套餐总量
            Integer allSetmealNumber = reportDao.searchAllSetmealNumber();

            //计算套餐占比
            hotSetmeal = hotSetmealOld.stream().map(hotSetmealInfo -> {
                //精度浮点数，四舍五入
                hotSetmealInfo.setProportion(BigDecimal.valueOf((1.0 * hotSetmealInfo.getSetmeal_count() / allSetmealNumber)).setScale(3, RoundingMode.HALF_UP));
                return hotSetmealInfo;
            }).collect(Collectors.toList());
        }

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

    @Override
    public void exportBusinessReport(HttpServletResponse response) {

        BusinessReport businessReport = getBusinessReportData();
        //查询概览运营数据，提交给Excel模版文件
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/report_template.xlsx");
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(resourceAsStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
            XSSFRow row2 = sheet.getRow(2);
            XSSFCell cell = row2.getCell(5);
            cell.setCellValue(businessReport.getReportDate().toString());

            //会员数据统计
            //获取第四行
            XSSFRow row4 = sheet.getRow(4);
            //获取单元格
            XSSFCell newMember = row4.getCell(5);
            newMember.setCellValue(businessReport.getTodayNewMember());
            XSSFCell allMember = row4.getCell(7);
            allMember.setCellValue(businessReport.getTotalMember());

            //获取第五行
            XSSFRow row5 = sheet.getRow(5);
            //获取单元格
            XSSFCell weekNewMember = row5.getCell(5);
            weekNewMember.setCellValue(businessReport.getThisWeekNewMember());
            XSSFCell monthNewMember = row5.getCell(7);
            monthNewMember.setCellValue(businessReport.getThisMonthNewMember());

            //预约到诊数据统计
            //订单完成率
            //获取第六行
            XSSFRow row7 = sheet.getRow(7);
            XSSFCell newOrder = row7.getCell(5);
            newOrder.setCellValue(businessReport.getTodayOrderNumber());
            XSSFCell newVisits = row7.getCell(7);
            newVisits.setCellValue(businessReport.getTodayVisitsNumber());

            //获取第七行
            XSSFRow row8 = sheet.getRow(8);
            XSSFCell weekNewOrder = row8.getCell(5);
            weekNewOrder.setCellValue(businessReport.getThisWeekOrderNumber());
            XSSFCell weekNewVisits = row8.getCell(7);
            weekNewVisits.setCellValue(businessReport.getThisWeekVisitsNumber());

            //获取第八行
            XSSFRow row9 = sheet.getRow(9);
            XSSFCell monthNewOrder = row9.getCell(5);
            monthNewOrder.setCellValue(businessReport.getThisMonthOrderNumber());
            XSSFCell monthNewVisits = row9.getCell(7);
            monthNewVisits.setCellValue(businessReport.getThisMonthVisitsNumber());

            //热门套餐
            List<HotSetmealInfo> hotSetmeal = businessReport.getHotSetmeal();
            for (int i = 0; i < hotSetmeal.size(); i++) {
                HotSetmealInfo hotSetmealInfo = hotSetmeal.get(i);
                XSSFRow row11 = sheet.getRow(12+i);
                row11.getCell(4).setCellValue(hotSetmealInfo.getName());
                row11.getCell(5).setCellValue(hotSetmealInfo.getSetmeal_count());
                row11.getCell(6).setCellValue(hotSetmealInfo.getProportion().toString());
                row11.getCell(7).setCellValue("备注个der");
            }

            ServletOutputStream outputStream = response.getOutputStream();

            xssfWorkbook.write(outputStream);
            outputStream.close();
            xssfWorkbook.close();
            resourceAsStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
