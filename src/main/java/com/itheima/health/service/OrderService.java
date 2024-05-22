package com.itheima.health.service;

import com.itheima.health.pojo.Order;

import java.util.List;
import java.util.Map;

/**
 * 预约Service
 */
public interface OrderService {
    /**
     * 添加预约
     *
     * @param order
     * @return
     */
    Order add(Order order);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Order findById(Integer id);

    /**
     * 根据套餐统计预约数量
     * 返回数据格式：
     * [{"name":"套餐1","value":10}]
     *
     * @return
     */
    List<Map<String, Object>> countBySetmeal();
}
