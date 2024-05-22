package com.itheima.health.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.itheima.health.common.RedisConst;
import com.itheima.health.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;


/**
 * 短信服务实现类
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Autowired
    private RedisTemplate redisTemplate;

    private IAcsClient acsClient = new DefaultAcsClient(DefaultProfile.getProfile("cn-hangzhou", "<accessKeyId>", "<accessSecret>"));

    @Override
    public void sendValidateCode(String validateCodeType, String phone) {
        log.info("[短信-发送验证码]type:{},phone:{}", validateCodeType, phone);
        // 生成验证码（1000~9999）
        int code = (int) (1000 + new SecureRandom().nextDouble() * 8999);
        log.debug("[短信-发送验证码]code:{}", code);
        // 保持到缓存（redis）
        String redisKey = RedisConst.VALIDATE_CODE_PREFIX + validateCodeType + ":" + phone;
        log.info("redisKey is {}, code is {}", redisKey, code);
        redisTemplate.boundValueOps(redisKey).set(String.valueOf(code), 5 * 60L, TimeUnit.SECONDS);

        /////////////////////下面代码由阿里云代码模板生成//////////////////

        // 这里无需真的将短信发送出去，测试过程中，我们直接在日志中查看打印的验证码即可
        //构造请求参数
        /*CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "传智健康项目");
        request.putQueryParameter("TemplateCode", "SMS_175060789");
        request.putQueryParameter("TemplateParam", "	{\"assertCode\":\"" + code + "\"}");
        try {
            //发起请求
            CommonResponse response = acsClient.getCommonResponse(request);
            log.info("[短信-发送验证码]response:{}", response.getData());
        } catch (ClientException e) {
            log.error("[短信-发送验证码]", e);
        }*/
    }

    /**
     * 校验短信验证码
     *
     * @param validateCodeType
     * @param phone
     * @param code
     * @return
     */
    @Override
    public boolean checkValidateCode(String validateCodeType, String phone, String code) {
        log.info("[短信-校验验证码]type:{},phone:{},code:{}", validateCodeType, phone, code);
        String redisKey = RedisConst.VALIDATE_CODE_PREFIX + validateCodeType + ":" + phone;
        //查询redis里的值
        String expectVal = (String) redisTemplate.boundValueOps(redisKey).get();
        //对比是否匹配
        if (!StringUtils.isEmpty(expectVal) && expectVal.equals(code)) {
            //如果匹配则删除redis并返回true
            redisTemplate.delete(redisKey);
            return true;
        } else {
            //如果不匹配则返回false
            return false;
        }
    }
}