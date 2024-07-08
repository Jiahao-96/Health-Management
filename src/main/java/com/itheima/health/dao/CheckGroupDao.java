package com.itheima.health.dao;

import com.github.pagehelper.Page;
import com.itheima.health.pojo.entity.CheckGroup;
import com.itheima.health.pojo.entity.CheckItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CheckGroupDao {
    /**
     * 添加
     * @param checkGroup
     */
    void insert(CheckGroup checkGroup);

    /**
     * 添加检查组和检查项关系
     * @param checkGroupId
     * @param checkItemId
     */
    void insertCheckGroupAndCheckItem(@Param("checkGroupId") Integer checkGroupId, @Param("checkItemId") Integer checkItemId);

    /**
     * 根据条件分页查询
     * @param queryString
     * @return
     */
    Page<CheckGroup> selectByCondition(@Param("queryString") String queryString);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    CheckGroup selectById(@Param("id") Integer id);

    /**
     * 跟进检查组ID查询检查项ID集合
     * @param id
     * @return
     */
    List<Integer> selectCheckItemIdsByCheckGroupId(@Param("id") Integer id);

    /**
     * 更新
     * @param checkGroup
     */
    void update(CheckGroup checkGroup);

    /**
     * 根据检查组ID删除有的检查项关联关系
     * @param id
     */
    void deleteCheckGroupAndCheckItemByCheckGroupId(@Param("id") Integer id);

    /**
     * 查询所有数据
     * @return
     */
    List<CheckGroup> selectAll();

    Long countCheckGroupByCheckgroupId(@Param("checkGroupId") Integer checkGroupId);
    /**
     * 根据ID删除
     * @param id
     */
    void deleteById(@Param("id") Integer id);

    List<CheckItem> selectCheckItemsByCheckGroupId(Integer id);
}
