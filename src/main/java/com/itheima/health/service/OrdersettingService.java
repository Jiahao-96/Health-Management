package com.itheima.health.service;

import com.itheima.health.pojo.entity.OrderSetting;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrdersettingService {
    void insertOrderSetting(MultipartFile excelFile) ;

    List<OrderSetting> getOrderSettingByMonth(String year, String month);

    void editNumberByDate(OrderSetting orderSetting);
}
