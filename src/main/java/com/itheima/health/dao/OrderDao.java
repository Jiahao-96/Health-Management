package com.itheima.health.dao;

import com.itheima.health.pojo.entity.Order;
import com.itheima.health.pojo.entity.OrderInfo;

import java.time.LocalDate;
import java.util.Date;

public interface OrderDao {
    Integer searchOrderByMemberIdAndOrderDate(Integer id, Date orderDate);


    void insertOrder(Order newOrder);

    OrderInfo searchOrderSucessBySetmealId(Integer orderId);

    Integer searchVisitsNumberByDay(LocalDate nowDay);

    Integer searchVisitsNumberByWeek(LocalDate weekStartDay, LocalDate nowDay);

    Integer searchVisitsNumberByMonth(LocalDate monthStartDay, LocalDate nowDay);

}
