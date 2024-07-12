package com.itheima.health.controller;


import com.itheima.health.anno.LogInfo;
import com.itheima.health.common.MessageConst;
import com.itheima.health.pojo.entity.BusinessReport;
import com.itheima.health.pojo.entity.MemberReport;
import com.itheima.health.pojo.entity.SetmealReport;
import com.itheima.health.pojo.result.Result;
import com.itheima.health.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;
    /**
     * 统计会员数量
     * @return
     */
    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        MemberReport memberReport = reportService.getMemberReport();
        return new Result(true, MessageConst.GET_MEMBER_NUMBER_REPORT_SUCCESS,memberReport);
    }

    /**
     * 套餐预约占比
     * @return
     */
    @GetMapping("/getSetmealReport")
    public Result getSetmealReport(){
        SetmealReport setmealReport = reportService.getSetmealReport();
        return new Result(true, MessageConst.GET_SETMEAL_COUNT_REPORT_SUCCESS,setmealReport);
    }

    /**
     * 运营统计
     * @return
     */
    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        BusinessReport businessReport = reportService.getBusinessReportData();
        return new Result(true, MessageConst.GET_BUSINESS_REPORT_FAIL,businessReport);
    }

    /**
     * 导出报表
     * @return
     */
    @LogInfo
    @GetMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletResponse response){
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");
        reportService.exportBusinessReport(response);
        return new Result(true, MessageConst.GET_BUSINESS_REPORT_FAIL);
    }


}
