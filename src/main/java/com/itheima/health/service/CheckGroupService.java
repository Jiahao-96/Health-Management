package com.itheima.health.service;


import com.itheima.health.pojo.dto.QueryPageBeanDTO;
import com.itheima.health.pojo.entity.CheckGroup;
import com.itheima.health.pojo.result.PageResult;

import java.util.List;

/**
 * @author zhangmeng
 * @description 检查组Service
 * @date 2019/9/18
 **/
public interface CheckGroupService {

    /**
     * 添加
     * @param checkGroup
     * @param checkitemIds
     */
    void add(CheckGroup checkGroup, Integer[] checkitemIds);

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    PageResult pageQuery(QueryPageBeanDTO queryPageBean);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    CheckGroup findById(Integer id);

    /**
     * 根据检查组ID查询关联的检查项ID集和
     * @param id
     * @return
     */
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    /**
     * 编辑
     * @param checkGroup 检查组
     * @param checkitemIds 关联的检查项ID
     */
    void edit(CheckGroup checkGroup, Integer[] checkitemIds);

    /**
     * 查询所有
     * @return
     */
    List<CheckGroup> findAll();

    public void delete(int id);


    List<Integer> findCheckGroupIdsBySeymealId(Integer id);
}
