package com.itheima.health.pojo.dto;

import lombok.Data;

/**
 * 用户名密码登录参数封装
 */
@Data
public class LoginDTO {
    public String username;
    public String password;
}
