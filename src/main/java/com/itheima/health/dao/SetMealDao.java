package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.entity.CheckGroup;
import com.itheima.health.pojo.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangmeng
 * @description 套餐DAO
 * @date 2019/9/26
 **/
@Mapper
public interface SetMealDao {
    /**
     * 插入
     * @param setmeal
     */
    void insert(Setmeal setmeal);

    /**
     * 插入与检查组关联关系
     * @param setmealId
     * @param checkgroupId
     */
    void insertSetMealAndCheckGroup(@Param("setmealId") Integer setmealId, @Param("checkgroupId") Integer checkgroupId);

    /**
     * 根据条件分页查询
     * @param queryString
     * @return
     */
    Page<Setmeal> selectByCondition(@Param("queryString") String queryString);

    List<Setmeal> selectAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Setmeal selectById(@Param("id") Integer id);

    Setmeal findById(String id);

    List<CheckGroup> selectCheckGroupBySetmealId(String id);

    List<String> selectAllimg();

    void edit(Setmeal setmeal);

    void deleteSetmealById(Integer id);

}
