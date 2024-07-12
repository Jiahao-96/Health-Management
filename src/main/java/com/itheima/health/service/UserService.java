package com.itheima.health.service;

import com.itheima.health.pojo.entity.User;

import java.util.List;
import java.util.Set;

/**
 * @author zhangmeng
 * @description 用户服务接口
 * @date 2019/9/6
 **/
public interface UserService {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return
     */
    User findByUsername(String username);

    /**
     * 查询用户可访问页面
     * @param user
     * @return
     */
    List<String> searchMenu(User user);

}
