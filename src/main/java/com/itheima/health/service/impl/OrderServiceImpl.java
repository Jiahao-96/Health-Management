package com.itheima.health.service.impl;

import com.itheima.health.common.MessageConst;
import com.itheima.health.dao.OrderDao;
import com.itheima.health.dao.OrderSettingDao;
import com.itheima.health.exception.BusinessRuntimeException;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 预约ServiceImpl
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private OrderDao orderDao;

    @Transactional
    public Order add(Order order) {
        log.info("[预约]data:{}", order);

        //查询预约设置信息
        OrderSetting orderSetting = orderSettingDao.selectByOrderDate(order.getOrderDate());
        if (null == orderSetting) {
            //如果不存在-不能预约
            throw new BusinessRuntimeException(MessageConst.SELECTED_DATE_CANNOT_ORDER);
        } else if (orderSetting.getReservations() >= orderSetting.getNumber()) {
            //如果存在-且约满
            throw new BusinessRuntimeException(MessageConst.ORDER_FULL);
        }

        //查询是否有重复预约  同一个人同一天同一个套餐
        long count = orderDao.countByMemberAndDateAndSetMeal(order.getMemberId(), order.getOrderDate(), order.getSetmealId());
        if (count > 0) {
            throw new BusinessRuntimeException(MessageConst.HAS_ORDERED);
        }

        //插入数据
        orderDao.insert(order);

        //修改已预约数
        orderSettingDao.updateReservationsById(orderSetting.getId(), orderSetting.getReservations() + 1);

        return orderDao.selectById(order.getId());
    }

    @Override
    public Order findById(Integer id) {
        return orderDao.selectById(id);
    }

    @Override
    public List<Map<String, Object>> countBySetmeal() {
        return orderDao.countBySetMeal();
    }
}
