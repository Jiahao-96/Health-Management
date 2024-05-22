package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.exception.BusinessRuntimeException;
import com.itheima.health.pojo.OrderSetting;
import com.itheima.health.service.OrderSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/ordersetting")
@Slf4j
public class OrderSettingController {
    @Autowired
    private OrderSettingService orderSettingService;
    /**
     * 上传预约设置的excel文件
     * @param multipartFile
     * @return
     */
    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile") MultipartFile multipartFile) {
        log.info("[预约设置-上传]fileName:{},size:{}", multipartFile.getOriginalFilename(), multipartFile.getSize());
        String filename = multipartFile.getOriginalFilename();
        if(StringUtils.isEmpty(filename)){
            return new Result(false,"缺少文件名");
        }
        //1 抽取excel数据 poi
        Workbook workbook = null;
        try (InputStream is = multipartFile.getInputStream();) {
            //1.1构造workbook
            if(filename.endsWith(".xls")){
                // excel 2003
                workbook = new HSSFWorkbook(is);
            }else if(filename.endsWith(".xlsx")){
                // excel 2007
                workbook = new XSSFWorkbook(is);
            }else{
                return new Result(false,"文件格式不正确，请检查重试");
            }
        } catch (IOException e) {
            log.error("",e);
            return new Result(false, "文件上传失败");
        }
        //1.2遍历取数据
        List<OrderSetting> orderSettings = new ArrayList<>();
        for (Sheet sheet : workbook) {
            // 第0行为表头，直接从第1行取
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                //获取行
                Row row = sheet.getRow(i);
                //解析行
                OrderSetting orderSetting = parseOrderSetting(row);
                //加入列表
                orderSettings.add(orderSetting);
            }
        }
        log.info("[预约设置-上传]解析成功,result:{}",orderSettings);

        //2 rpc调用数据入库
        orderSettingService.addAll(orderSettings);
        return new Result(true,MessageConst.IMPORT_ORDERSETTING_SUCCESS);

    }

    /**
     * 从excel行提取OrderSetting数据
     * @param row
     * @return
     */
    private OrderSetting parseOrderSetting(Row row){
        Date date = null;
        Integer number = null;
        try {
            //获取日期
            if(null != row.getCell(0) ){
                //如果单元格没有数据，则getDateCellValue返回null
                date = row.getCell(0).getDateCellValue();
            }
            //获取最大预约数量
            if(null != row.getCell(1)){
                //如果单元格没有数据，则getNumericCellValue返回0
                double numberValue = row.getCell(1).getNumericCellValue();
                //将double型转为int型
                number =  Double.valueOf(numberValue).intValue();
            }
        }catch (IllegalStateException|NumberFormatException e){
            log.error("",e);
            throw new BusinessRuntimeException(String.format("数据格式错误,%s 第%d行",row.getSheet().getSheetName(),row.getRowNum()+1));
        }
        // 判断是否缺少数据
        if(null == date || null == number){
            throw new BusinessRuntimeException(String.format("缺少必填数据,%s 第%d行",row.getSheet().getSheetName(),row.getRowNum()+1));
        }
        return new OrderSetting(date,number);
    }

    /**
     * 根据月份查询数据
     *
     * @param year  年
     * @param month 月
     * @return
     */
    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(@RequestParam Integer year, @RequestParam Integer month) {
        //调用RPC查询数据
        List<OrderSetting> orderSettings = orderSettingService.getOrderSettingByMonth(year, month);
        //转换成前端需要的结构
        List<Map<String, Object>> voList = new ArrayList<>();
        for (OrderSetting orderSetting : orderSettings) {
            //创建用于日期处理的工具类Calendar对象
            Calendar cal = Calendar.getInstance();
            //设置时间为预约日期
            cal.setTime(orderSetting.getOrderDate());
            Map<String, Object> map = new HashMap<>();
            //设置日期限-当月第几天
            map.put("date", cal.get(Calendar.DAY_OF_MONTH));
            //设置数量-当天允许的预约数量
            map.put("number", orderSetting.getNumber());
            //设置已经预约的人数
            map.put("reservations", orderSetting.getReservations());
            voList.add(map);
        }
        return new Result(true, MessageConst.GET_ORDERSETTING_SUCCESS, voList);
    }
    /**
     * 根据日期编辑
     * @param orderSetting
     * @return
     */
    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        log.info("[预约设置-根据日期编辑]data:{}",orderSetting);
        orderSettingService.editNumberByDate(orderSetting.getOrderDate(),orderSetting.getNumber());
        return new Result(true,MessageConst.ORDERSETTING_SUCCESS);
    }
}
