package com.itheima.health.dao;

import com.itheima.health.pojo.entity.HotSetmealInfo;
import com.itheima.health.pojo.entity.SetmealNameValue;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportDao {
    Integer searchMemberNumberByMonth(LocalDate startDay,LocalDate endDay);

    List<String> searchSetmealName();

    List<SetmealNameValue> searchSetmealNameAndOrderNumber();

    Integer searchAllSetmealNumber();

    List<HotSetmealInfo> searchHotSetmealNameAndOrderNumber();


}
