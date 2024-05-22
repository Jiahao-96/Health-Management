package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码Controller
 */
@RestController
@RequestMapping("/mobile/validateCode")
@Slf4j
public class MobileValidateCodeController {

    @Autowired
    private SmsService smsService;

    /**
     * 发送验证码
     *
     * @param type      验证码类型，用于区分不同的业务
     * @param telephone 手机号
     * @return
     */
    @RequestMapping("/send")
    public Result send(String type, String telephone) {
        log.info("[验证码-发送]type:{},telephone:{}", type, telephone);
        //rpc调用发送验证码
        smsService.sendValidateCode(type, telephone);
        return new Result(true, MessageConst.SEND_VALIDATECODE_SUCCESS);
    }
}
