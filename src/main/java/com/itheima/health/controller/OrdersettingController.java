package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.pojo.entity.OrderSetting;
import com.itheima.health.pojo.result.Result;
import com.itheima.health.service.OrdersettingService;
import lombok.Data;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/ordersetting")
public class OrdersettingController {
    @Autowired
    private OrdersettingService ordersettingService;

    /**
     * 解析上传的Excel文件
     * @param excelFile
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile excelFile)  {
        ordersettingService.insertOrderSetting(excelFile);
        return new Result(true, MessageConst.IMPORT_ORDERSETTING_SUCCESS);
    }

    /**
     *
     * @param year
     * @param month
     * @return
     */

    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String year ,String month){
        List<OrderSetting> orderSettings = ordersettingService.getOrderSettingByMonth(year,month);
        return new Result(true,MessageConst.GET_ORDERSETTING_SUCCESS,orderSettings);
    }

    @PostMapping("/editNumberByDate")
    public Result editNumberByDate (@RequestBody OrderSetting orderSetting){
        ordersettingService.editNumberByDate(orderSetting);
        return new Result(true,MessageConst.ORDERSETTING_SUCCESS);
    }
}
