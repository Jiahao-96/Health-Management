package com.itheima.health.controller;


import com.itheima.health.common.MessageConst;
import com.itheima.health.pojo.entity.MemberReport;
import com.itheima.health.pojo.result.Result;
import com.itheima.health.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("getMemberReport")
    public Result getMemberReport(){
        MemberReport memberReport = reportService.getMemberReport();
        return new Result(true, MessageConst.GET_MEMBER_NUMBER_REPORT_SUCCESS,memberReport);
    }
}
