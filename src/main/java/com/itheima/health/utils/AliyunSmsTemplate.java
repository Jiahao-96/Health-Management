package com.itheima.health.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 发送短信的模板类，提供了sendSms(String mobile)方法，用于发送短信验证码
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "health.aliyun.sms")
public class AliyunSmsTemplate {

    private String signName;
    private String templateCode;
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint = "dysmsapi.aliyuncs.com";

    /**
     * 使用AK&SK初始化账号Client
     *
     * @return Client
     */
    public Client createClient() throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = endpoint;
        return new Client(config);
    }

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     * @return 发送的验证码值
     */
    public String sendSms(String mobile) {
        try {
            //100000~999999   [100000,1000000)  [0, 900000)
            int code = 100000 + new Random().nextInt(900000);
            String smsCode = "{\"code\": \"" + code + "\"}";

            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(mobile)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(smsCode);
            
            SendSmsResponse sendSmsResponse = createClient().sendSms(sendSmsRequest);
            String responseCode = sendSmsResponse.getBody().getCode();
            if ("OK".equals(responseCode)) {
                return String.valueOf(code);
            } else {
                log.warn("发送短信失败，错误码：{}，错误消息：{}", responseCode, sendSmsResponse.getBody().getMessage());
                return null;
            }
        } catch (Exception e) {
            log.error("发送短信失败", e);
            return null;
        }
    }
}