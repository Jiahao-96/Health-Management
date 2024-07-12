package com.itheima.health.dao;

import com.itheima.health.pojo.entity.OrderSetting;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Mapper
public interface OrdersettingDao {
    void insertOrderSetting(OrderSetting orderSetting);

    List<OrderSetting> getOrderSettingByMonth(LocalDate start, LocalDate end);

    Integer editNumberByDate(OrderSetting orderSetting);

    Integer searchOrdersettingNumber(Date orderDate);

    Integer searchOrdersettingNumberByDay(LocalDate nowDay);

    Integer searchOrdersettingNumberByWeek(LocalDate weekStartDay, LocalDate nowDay);

    Integer searchOrdersettingNumberByMonth(LocalDate monthStartDay, LocalDate nowDay);

}
