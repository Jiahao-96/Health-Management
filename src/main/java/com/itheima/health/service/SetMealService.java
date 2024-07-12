package com.itheima.health.service;


import com.itheima.health.pojo.dto.QueryPageBeanDTO;
import com.itheima.health.pojo.entity.Setmeal;
import com.itheima.health.pojo.result.PageResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhangmeng
 * @description 套餐SERVICE
 * @date 2019/9/26
 **/
public interface SetMealService {

    /**
     * 添加套餐
     * @param setmeal
     * @param checkgroupIds
     */
    void add(Setmeal setmeal, Integer[] checkgroupIds);

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    PageResult findPage(QueryPageBeanDTO queryPageBean);

    /**
     * 查询套餐列表
     * @return
     */
    List<Setmeal> getSetmeal();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Setmeal findById(Integer id);


    /**
     * 编辑套餐
     * @param setmeal
     * @param checkGroupIds
     */
    void edit(Setmeal setmeal, List<Integer> checkGroupIds);


    /**
     * 删除套餐
     * @param id
     */
    void deleteSetmealById(Integer id);
}
