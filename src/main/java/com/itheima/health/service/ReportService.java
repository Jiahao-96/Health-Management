package com.itheima.health.service;

import com.itheima.health.pojo.entity.BusinessReport;
import com.itheima.health.pojo.entity.MemberReport;
import com.itheima.health.pojo.entity.SetmealReport;

public interface ReportService {
    MemberReport getMemberReport();

    SetmealReport getSetmealReport();

    BusinessReport getBusinessReportData();

}
