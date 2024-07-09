package com.itheima.health.service;

import com.itheima.health.pojo.dto.SubmitDTO;
import com.itheima.health.pojo.dto.ValidateCodeDTO;
import com.itheima.health.pojo.entity.Order;
import com.itheima.health.pojo.entity.OrderInfo;
import com.itheima.health.pojo.entity.Setmeal;

import java.util.List;

public interface MobileService {
    void send(String type, String telephone);

    void smsLogin(ValidateCodeDTO validateCodeDTO);

    List<Setmeal> getSetmeal();

    Setmeal findById(String id);

    Order submit(SubmitDTO submitDTO);

    OrderInfo searchOrderSucessBySetmealId(Integer id);

}
