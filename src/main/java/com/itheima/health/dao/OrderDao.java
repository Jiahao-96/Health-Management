package com.itheima.health.dao;

import com.itheima.health.pojo.entity.Order;
import com.itheima.health.pojo.entity.OrderInfo;

import java.util.Date;

public interface OrderDao {
    Integer searchOrderByMemberIdAndOrderDate(Integer id, Date orderDate);


    void insertOrder(Order newOrder);

    OrderInfo searchOrderSucessBySetmealId(Integer orderId);

}
