package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.pojo.Order;
import com.itheima.health.pojo.Setmeal;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.OrderService;
import com.itheima.health.service.SetMealService;
import com.itheima.health.service.SmsService;
import com.itheima.health.vo.OrderSubmitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 预约控制器
 */
@RestController
@RequestMapping("/mobile/order")
@Slf4j
public class MobileOrderController {

    @Autowired
    private SmsService smsService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SetMealService setMealService;

    /**
     * 提交预约信息
     *
     * @param param
     * @return
     */
    @RequestMapping("/submit")
    public Result submit(@RequestBody OrderSubmitParam param) {
        log.info("[提交预约信息]data：{}", param);

        //1-验证码验证  smsService
        if (!smsService.checkValidateCode("ORDER", param.getTelephone(), param.getValidateCode())) {
            return new Result(false, MessageConst.VALIDATECODE_ERROR);
        }

        //2-创建会员 memberService
        Member member = new Member();
        member.setIdCard(param.getIdCard());
        member.setPhoneNumber(param.getTelephone());
        member.setName(param.getName());
        member.setSex(param.getSex());
        member = memberService.createOrUpdate(member);

        //3-提交体检信息  orderService
        Order order = new Order();
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        order.setSetmealId(param.getSetMealId());
        order.setOrderType(Order.ORDERTYPE_WEIXIN);
        order.setOrderDate(param.getOrderDate());
        order.setMemberId(member.getId());
        order = orderService.add(order);

        //4-返回结果
        return new Result(true, MessageConst.ORDER_SUCCESS,order);
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id){
        //rpc查询数据
        Order order = orderService.findById(id);
        Member member = memberService.findById(order.getMemberId());
        Setmeal setmeal = setMealService.findById(order.getSetmealId());

        //封装返回结果
        Map<String,Object> map = new HashMap<>();
        map.put("member",member.getName());
        map.put("setmeal",setmeal.getName());
        map.put("orderDate",new SimpleDateFormat("yyyy-MM-dd").format(order.getOrderDate()));
        map.put("orderType",order.getOrderType());
        return new Result(true,MessageConst.QUERY_ORDER_SUCCESS,map);
    }
}
