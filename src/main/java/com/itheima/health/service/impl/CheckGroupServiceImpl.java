package com.itheima.health.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.health.dao.CheckGroupDao;
import com.itheima.health.exception.BusinessRuntimeException;
import com.itheima.health.pojo.dto.QueryPageBeanDTO;
import com.itheima.health.pojo.entity.CheckGroup;
import com.itheima.health.pojo.result.PageResult;
import com.itheima.health.service.CheckGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhangmeng
 * @description
 * @date 2019/9/18
 **/
@Service
@Slf4j
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;
    @Transactional
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        log.info("[检查组-添加]data:{},checkitemIds：{}", checkGroup, checkitemIds);
        // 插入基本数据
        checkGroupDao.insert(checkGroup);
        //逐条插入关系表数据
        for (Integer checkItemId : checkitemIds) {
            checkGroupDao.insertCheckGroupAndCheckItem(checkGroup.getId(),checkItemId);
        }
    }

    @Override
    public PageResult pageQuery(QueryPageBeanDTO queryPageBean) {
        log.info("[检查组-分页查询]data：{}",queryPageBean);
        //设置分页参数
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //查询page
        Page<CheckGroup>  page= checkGroupDao.selectByCondition(queryPageBean.getQueryString());
        //封装返回值
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.selectById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.selectCheckItemIdsByCheckGroupId(id);
    }
    @Transactional
    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1、更新检查组
        checkGroupDao.update(checkGroup);
        //2、更新关联关系
        //2.1、删除原有关系
        checkGroupDao.deleteCheckGroupAndCheckItemByCheckGroupId(checkGroup.getId());
        //2.2、加入当前关联关系
        for (Integer checkitemId : checkitemIds) {
            checkGroupDao.insertCheckGroupAndCheckItem(checkGroup.getId(),checkitemId);
        }


    }

    @Override
    public List<CheckGroup> findAll() {
        //调用DAO查询数据并返回
        return checkGroupDao.selectAll();
    }

    @Transactional
    @Override
    public void delete(int id) {
        log.info("[检查项-根据id删除]id：{}",id);
        //校验是否可以删除
        Long count = checkGroupDao.countCheckGroupByCheckgroupId(id);
        if(count >0){
            throw new BusinessRuntimeException("当前检查组有数据，不能删除");
        }
        //2.1、删除原有关系
        checkGroupDao.deleteCheckGroupAndCheckItemByCheckGroupId(id);
        //实际删除操作
        checkGroupDao.deleteById(id);
    }

    @Override
    public List<Integer> findCheckGroupIdsBySeymealId(Integer id) {
        List<Integer> checkGroupIds = checkGroupDao.findCheckGroupIdsBySeymealId(id);
        return checkGroupIds;
    }
}
