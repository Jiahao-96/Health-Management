package com.itheima.health.dao;

import com.itheima.health.pojo.entity.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperateLogDao {
    //插入日志数据
    @Insert("insert into t_operate_log (operate_user_id,operate_user_name, operate_time, class_name, method_name, method_params, return_value, cost_time) " +
            "values (#{operateUserId}, #{operateUserName},#{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime});")
    public void insert(OperateLog log);
}
