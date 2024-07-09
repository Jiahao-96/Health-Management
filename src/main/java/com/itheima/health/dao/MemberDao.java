package com.itheima.health.dao;

import com.itheima.health.pojo.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDao {
    void insertMember(Member memberNew);

    Member searchMemberByPhoneNumber(String phoneNumber);


}
