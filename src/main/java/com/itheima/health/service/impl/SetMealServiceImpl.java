package com.itheima.health.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.dao.SetMealDao;
import com.itheima.health.pojo.dto.QueryPageBeanDTO;
import com.itheima.health.pojo.entity.Setmeal;
import com.itheima.health.pojo.result.PageResult;
import com.itheima.health.service.SetMealService;
import com.itheima.health.properties.AliOssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private CheckGroupDao checkGroupDao;

    @Transactional
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
        for (Setmeal setmeal : page) {
            setmeal.setImg(aliOssProperties.getUrlPrefix()+setmeal.getImg());
        }
        //封装返回值
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Setmeal> getSetmeal() {
        List<Setmeal> setmeals = setMealDao.selectAll();

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


    @Transactional
    @Override
    public void edit(Setmeal setmeal, List<Integer> checkGroupIds) {
        //1.修改基本信息(图片链接需要处理)
        setMealDao.edit(setmeal);
        //2.删除原有的绑定信息，插入新的绑定
        Integer setmealId = setmeal.getId();
        checkGroupDao.deleteSetmealAndCheckGroupIdBySetmealId(setmealId);
        //不为空就插入
        if (checkGroupIds !=null){
            checkGroupIds.forEach(checkGroupid ->{
                setMealDao.insertSetMealAndCheckGroup(setmealId,checkGroupid);
            });
        }
    }

    @Transactional
    @Override
    public void deleteSetmealById(Integer id) {
        //删除基本信息
        setMealDao.deleteSetmealById(id);
        //删除绑定检查组信息
        checkGroupDao.deleteSetmealAndCheckGroupIdBySetmealId(id);
    }

}
