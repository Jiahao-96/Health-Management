package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Member;
import com.itheima.health.service.MemberService;
import com.itheima.health.service.SmsService;
import com.itheima.health.vo.SmsLoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/mobile/login")
@Slf4j
public class MobileLoginController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private MemberService memberService;

    @PostMapping("smsLogin")
    public Result login(@RequestBody SmsLoginParam param, HttpServletRequest request){
        //验证码校验
        boolean success = smsService.checkValidateCode("MOBILE_SIGNIN", param.getTelephone(), param.getValidateCode());
        if (!success) {
            return new Result(false, MessageConst.VALIDATECODE_ERROR);
        }

        //创建用户
        Member member = new Member();
        member.setPhoneNumber(param.getTelephone());
        member = memberService.createOrUpdate(member);
        request.getSession().setAttribute("member",member);
        //登录（模拟，直接输出成功）
        log.info("[登录成功]》》》》》》》》》》》》》》》》》》》》》》》》{}", member.getId());
        return new Result(true, MessageConst.LOGIN_SUCCESS);
    }
}
