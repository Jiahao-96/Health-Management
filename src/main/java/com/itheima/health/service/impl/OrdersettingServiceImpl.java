package com.itheima.health.service.impl;

import com.itheima.health.dao.OrdersettingDao;
import com.itheima.health.pojo.entity.OrderSetting;
import com.itheima.health.service.OrdersettingService;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class OrdersettingServiceImpl implements OrdersettingService {
    @Autowired
    private OrdersettingDao ordersettingDao;
    @Override
    public void insertOrderSetting(MultipartFile excelFile) {
        //在内存中创建一个Excel文件对象
        OrderSetting orderSetting = null;
        try {
            XSSFWorkbook excel = new XSSFWorkbook(excelFile.getInputStream());
            XSSFSheet sheet = excel.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                Date orderDate = sheet.getRow(i).getCell(0).getDateCellValue();
                //返回的是一个double类型，直接int强转就行，不要转换成包装类，不然不好转成int
                int number = (int)sheet.getRow(i).getCell(1).getNumericCellValue();
                orderSetting = new OrderSetting(orderDate,number);
                ordersettingDao.insertOrderSetting(orderSetting);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
    @Override
    public List<OrderSetting> getOrderSettingByMonth(String year, String month) {
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        List<OrderSetting> orderSettings = ordersettingDao.getOrderSettingByMonth(start,end);
        for (OrderSetting orderSetting : orderSettings) {
            Date orderDate = orderSetting.getOrderDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(orderDate);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            orderSetting.setDate(day);
        }
        return orderSettings;

    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        ordersettingDao.editNumberByDate(orderSetting);
    }
}
