package com.itheima.health.service.impl;

import com.itheima.health.dao.UserDao;
import com.itheima.health.pojo.entity.User;
import com.itheima.health.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author zhangmeng
 * @description 用户服务实现类
 * @date 2019/9/6
 **/
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User findByUsername(String username) {
        //调用DAO查询数据
        return userDao.selectByUsername(username);
    }

    @Override
    public List<String> searchMenu(User user) {
        List<String> menuList = userDao.searchMenu(user);
        return menuList;

    }
}
