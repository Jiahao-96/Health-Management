package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.PageResult;
import com.itheima.health.entity.QueryPageBean;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.CheckItem;
import com.itheima.health.service.CheckItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhangmeng
 * @description 检查项Controller
 * @date 2019/9/16
 **/
@RestController
@RequestMapping("/checkitem")
@Slf4j
public class CheckItemController {

    @Autowired
    private CheckItemService checkItemService;

    /**
     * 新增
     * 1、rpc调用完成新增业务
     * 2、返回结果
     * @param checkItem
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody CheckItem checkItem){
        log.info("[检查项-新增]data:{}",checkItem);
        checkItemService.add(checkItem);
        return new Result(true,MessageConst.ADD_CHECKITEM_SUCCESS);
    }

    /**
     * 分页查询
     *
     * @param queryPageBean
     * @return
     */
    @GetMapping("findPage")
    public Result findPage(QueryPageBean queryPageBean) {
        log.info("[检查项-分页查询]data:{}", queryPageBean);
        //rpc查询数据
        PageResult pageResult = checkItemService.pageQuery(queryPageBean);
        //封装返回结果
        return new Result(true,MessageConst.ACTION_SUCCESS, pageResult);
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Integer id){
        log.info("[检查项-根据id删除]id：{}",id);
        //RPC调用处理业务
        checkItemService.delete(id);
        return new Result(true,MessageConst.DELETE_CHECKITEM_SUCCESS);
    }

    /**
     * 编辑
     *
     * @param checkItem
     * @return
     */
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem) {
        log.info("[检查项-编辑]data:{}", checkItem);
        checkItemService.edit(checkItem);
        return new Result(true, MessageConst.EDIT_CHECKITEM_SUCCESS);
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id) {
        log.info("[检查项-根据ID查询]id:{}", id);
        CheckItem checkItem = checkItemService.findById(id);
        return new Result(true,MessageConst.ACTION_SUCCESS,checkItem);
    }

    /**
     * 查询所有检查项
     * @return
     */
    @RequestMapping("/findAll")
    public Result findAll(){
        log.info("[检查项-查询所有]~");
        List<CheckItem> checkItems = checkItemService.findAll();
        return new Result(true,MessageConst.QUERY_CHECKITEM_SUCCESS,checkItems);
    }
}
