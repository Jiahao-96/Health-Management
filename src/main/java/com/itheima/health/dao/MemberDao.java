package com.itheima.health.dao;

import com.itheima.health.pojo.entity.Member;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface MemberDao {
    void insertMember(Member memberNew);

    Member searchMemberByPhoneNumber(String phoneNumber);

    Integer searchMemberNumberByDayOrAll(LocalDate nowDay);

    Integer searchMemberNumberByWeek(LocalDate weekStartDay, LocalDate nowDay);

    Integer searchMemberNumberByMonth(LocalDate monthStartDay, LocalDate nowDay);
}
