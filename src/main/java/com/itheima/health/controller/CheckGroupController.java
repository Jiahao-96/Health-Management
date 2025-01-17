package com.itheima.health.controller;


import com.itheima.health.anno.L2Cache;
import com.itheima.health.anno.LogInfo;
import com.itheima.health.common.CacheType;
import com.itheima.health.common.MessageConst;
import com.itheima.health.pojo.dto.QueryPageBeanDTO;
import com.itheima.health.pojo.entity.CheckGroup;
import com.itheima.health.pojo.result.PageResult;
import com.itheima.health.pojo.result.Result;
import com.itheima.health.service.CheckGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkgroup")
@Slf4j
@RequiredArgsConstructor
public class CheckGroupController {

    private final CheckGroupService checkGroupService;
    /**
     * 添加
     *
     * @return
     */
    @LogInfo
    @RequestMapping("/add")
    @L2Cache(cacheName = "check",key = "'checkGroup'", type = CacheType.DELETE)
    public Result add(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {
        log.info("[检查组-添加]data:{},checkitemIds:{}", checkGroup, checkitemIds);
        //RPC请求添加
        checkGroupService.add(checkGroup, checkitemIds);
        return new Result(true, MessageConst.ADD_CHECKGROUP_SUCCESS);
    }

    /**
     * 分页查询
     *
     * @param queryPageBean
     * @return
     */
    @GetMapping("findPage")
    @L2Cache(cacheName = "checkGroup", key ="#queryPageBean.currentPage " +
            "+ ':' + #queryPageBean.pageSize" +
            "+ '_' + #queryPageBean.queryString",
            type = CacheType.FULL)
    public Result findPage(QueryPageBeanDTO queryPageBean) {
        log.info("[检查组-分页查询]data:{}", queryPageBean);
        //rpc查询结果
        PageResult pageResult = checkGroupService.pageQuery(queryPageBean);
        //构造返回对象
        return new Result(true,MessageConst.QUERY_CHECKGROUP_SUCCESS,pageResult);
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @L2Cache(cacheName = "checkGroup", key ="'findById' + #id", type = CacheType.FULL)
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        log.info("[检查组-根据id查询]id:{}", id);

        //rpc 调用业务处理
        CheckGroup checkGroup = checkGroupService.findById(id);
        //封装返回值
        return new Result(true, MessageConst.QUERY_CHECKGROUP_SUCCESS, checkGroup);

    }

    /**
     * 根据检查组ID查询关联的检查项ID集和
     *
     * @param id
     * @return
     */
    @L2Cache(cacheName = "checkGroup", key ="'findCheckItemIds' + #id", type = CacheType.FULL)
    @RequestMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckGroupId(Integer id) {
        log.info("[检查组-根据检查组ID查询关联的检查项ID集和]id:{}", id);

        //rpc 调用业务处理
        List<Integer> itemIds = checkGroupService.findCheckItemIdsByCheckGroupId(id);
        //封装返回值
        return new Result(true, MessageConst.ACTION_SUCCESS, itemIds);
    }

    /**
     * 编辑
     *
     * @return
     */
    @L2Cache(cacheName = "check",key = "'checkGroup'", type = CacheType.DELETE)
    @LogInfo
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds) {
        log.info("[检查组-编辑]data:{},checkitemIds:{}", checkGroup, checkitemIds);

        //RPC请求添加
        checkGroupService.edit(checkGroup, checkitemIds);
        return new Result(true, MessageConst.EDIT_CHECKGROUP_SUCCESS);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @L2Cache(cacheName = "checkGroup", key ="'findAll'", type = CacheType.FULL)
    @RequestMapping("/findAll")
    public Result findAll() {
        //rpc调用查询数据
        List<CheckGroup> checkGroups = checkGroupService.findAll();
        //封装返回结果
        return new Result(true, MessageConst.QUERY_CHECKGROUP_SUCCESS, checkGroups);
    }

    /**
     * 根据id删除检查组
     * @param id
     * @return
     */
    @LogInfo
    @PostMapping("/deleteCheckGroupitemById")
    @L2Cache(cacheName = "check",key = "'checkGroup'", type = CacheType.DELETE)
    public Result deleteCheckGroupitemById(@RequestParam("id") Integer id){
        try {
            checkGroupService.delete(id);
            return  new Result(true, MessageConst.DELETE_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false, MessageConst.DELETE_CHECKGROUP_FAIL);
        }

    }
    /**
     * 编辑套餐信息时的管理检查组显示
     * @param id
     * @return
     */
    @GetMapping("/findCheckGroupIdsBySetmealId")
    @L2Cache(cacheName = "checkGroup", key ="'findCheckGroupIdsBySetmealId' + #id", type = CacheType.FULL)
    public Result findCheckGroupIdsBySeymealId(@RequestParam Integer id){
        List<Integer> checkGroupIds = checkGroupService.findCheckGroupIdsBySeymealId(id);
        return new Result(true,MessageConst.ACTION_SUCCESS,checkGroupIds);
    }

}
