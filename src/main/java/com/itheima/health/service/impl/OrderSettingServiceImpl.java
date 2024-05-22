package com.itheima.health.service.impl;

import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;

    @Transactional
    @Override
    public void addAll(List<OrderSetting> orderSettings) {
        log.info("[预约设置-批量添加]data：{}", orderSettings);
        //循环操作
        for (OrderSetting orderSetting : orderSettings) {
            editNumberByDate(orderSetting.getOrderDate(), orderSetting.getNumber());
        }
    }

    @Override
    public List<OrderSetting> getOrderSettingByMonth(int year, int month) {
        //构造日期范围
        LocalDate startDate = LocalDate.of(year, month, 1); //获取该月第一天日期
        LocalDate endDate = YearMonth.from(startDate).atEndOfMonth();  // 获取该月最后一天日期

        // 下面构造结束日期的代码有bug，如果该月没有31天，那么sql语句将查询出来的数据为空
        // String startDate = String.format("%d-%d-1", year, month);
        // String endDate = String.format("%d-%d-31", year, month);
        return orderSettingDao.selectByOrderDateRange(startDate, endDate);
    }

    @Transactional
    @Override
    public void editNumberByDate(Date orderDate, int number) {
        log.info("[预约设置-根据日期编辑]date:{},number:{}", orderDate, number);
        // 判断是否已存在
        OrderSetting po = orderSettingDao.selectByOrderDate(orderDate);
        if (null != po) {
            // 存在则更新
            orderSettingDao.updateNumberById(po.getId(), number);
        } else {
            // 不存在则插入
            orderSettingDao.insert(new OrderSetting(orderDate, number));
        }
    }
}
