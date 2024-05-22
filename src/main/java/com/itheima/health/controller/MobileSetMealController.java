package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangmeng
 * @description 套餐控制器
 * @date 2019/9/26
 **/
@RestController
@RequestMapping("/mobile/setmeal")
@Slf4j
public class MobileSetMealController {
    @Autowired
    private SetMealService setMealService;

    /**
     * 用户端mobile--获取所有套餐数据
     * @return
     */
    @GetMapping("/getSetmeal")
    public Result getSetmeal(){
        log.info("【用户端mobile---获取所有套餐数据】");
        List<Setmeal> list = setMealService.getSetmeal();
        return new Result(true, MessageConst.QUERY_SETMEALLIST_SUCCESS, list);
    }

    /**
     * 用户端mobile---查询套餐详情
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(Integer id){
        log.info("【用户端mobile---查询套餐详情：{}】", id);
        Setmeal setmeal = setMealService.findById(id);
        return new Result(true,MessageConst.QUERY_SETMEAL_SUCCESS,setmeal);
    }
}
