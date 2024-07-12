package com.itheima.health.dao;

import com.itheima.health.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author ：zhang
 * @date ：Created in 2019/11/20
 * @description ：用户DAO
 * @version: 1.0
 */
@Mapper
public interface UserDao {

    /**
     * 根据userName查询
     * @param username
     * @return
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 查询可访问菜单
     * @param user
     * @return
     */
    List<String> searchMenu(User user);

}
