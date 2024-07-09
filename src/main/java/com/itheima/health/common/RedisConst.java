package com.itheima.health.common;

/**
 * @author zhangmeng
 * @description
 * @date 2019/9/26
 **/
public class RedisConst {

    //验证码前缀
    public static final String VALIDATE_CODE_PREFIX = "validateCode:";

    //上传至阿里云的图片在redis中的key名
    public static final String SETMEAL_PIC_RESOURCES = "validateCode:";

    //登录
    public static final String MOBILE_SIGNIN = "MOBILE_SIGNIN";

    //预约
    public static final String ORDER = "ORDER";

    //redis中存的数据库图片名
    public static final String MYSQL_PIC = "mysqlImgs";
    //redis中存的阿里云图片名
    public static final String ALIYUN_PIC = "aliyunImgs";


}
