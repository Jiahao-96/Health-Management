package com.itheima.health.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.SetMealDao;
import com.itheima.health.pojo.dto.QueryPageBeanDTO;
import com.itheima.health.pojo.entity.Setmeal;
import com.itheima.health.pojo.result.PageResult;
import com.itheima.health.service.SetMealService;
import com.itheima.health.utils.AliOssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangmeng
 * @description 套餐SEVICE实现类
 * @date 2019/9/26
 **/
@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealDao setMealDao;
    @Autowired
    private AliOssProperties aliOssProperties;

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        log.info("[套餐-添加]data:{},checkgroupIds:{}",setmeal,checkgroupIds);
        // 调用DAO数据入库
        // 插入基本数据
        setMealDao.insert(setmeal);
        // 插入关联数据
        for (Integer checkgroupId : checkgroupIds) {
            setMealDao.insertSetMealAndCheckGroup(setmeal.getId(),checkgroupId);
        }

    }

    @Override
    public PageResult findPage(QueryPageBeanDTO queryPageBean) {
        //设置分页参数
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //DAO查询数据
        Page<Setmeal> page = setMealDao.selectByCondition(queryPageBean.getQueryString());
        //封装返回值
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Setmeal> getSetmeal() {
        List<Setmeal> setmeals = setMealDao.selectAll();
        setmeals.forEach(setmeal -> {
            // 构造图片完整路径
            setmeal.setImg(aliOssProperties.getUrlPrefix()+setmeal.getImg());
        });
        return setmeals;
    }

    @Override
    public Setmeal findById(Integer id) {
        //调用DAO查询数据
        Setmeal setmeal = setMealDao.selectById(id);
        // 构造图片完整路径
        setmeal.setImg(aliOssProperties.getUrlPrefix()+setmeal.getImg());
        return setmeal;
    }
}
