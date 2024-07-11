package com.itheima.health.dao;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public interface ReportDao {
    Integer searchMemberNumberByMonth(LocalDate startDay,LocalDate endDay);

}
