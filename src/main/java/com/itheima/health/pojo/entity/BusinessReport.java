package com.itheima.health.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BusinessReport {
    private LocalDate reportDate;
    private Integer todayVisitsNumber;
    private Integer thisWeekVisitsNumber;
    private Integer thisMonthVisitsNumber;

    private Integer todayNewMember;
    private Integer thisMonthNewMember;
    private Integer thisWeekNewMember;

    private List<HotSetmealInfo> hotSetmeal;
    private Integer totalMember;

    private Integer thisMonthOrderNumber;
    private Integer todayOrderNumber;
    private Integer thisWeekOrderNumber;
}
