package com.itheima.health.service;

import com.itheima.health.pojo.entity.BusinessReport;
import com.itheima.health.pojo.entity.MemberReport;
import com.itheima.health.pojo.entity.SetmealReport;

import javax.servlet.http.HttpServletResponse;

public interface ReportService {
    MemberReport getMemberReport();

    SetmealReport getSetmealReport();

    BusinessReport getBusinessReportData();

    void exportBusinessReport(HttpServletResponse response);
}
