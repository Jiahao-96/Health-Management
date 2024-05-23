package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.pojo.result.Result;
import com.itheima.health.pojo.entity.User;
import com.itheima.health.service.UserService;
import com.itheima.health.pojo.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private static final String CURRENT_USER = "CURRENT_USER";
    @Autowired
    private UserService userService;

    /**
     * 根据用户名和密码登录
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result login(HttpServletRequest request, @RequestBody LoginDTO dto) {
        log.info("【登录】 dto:{}", dto);
        //调用service查询用户信息
        User user = userService.findByUsername(dto.getUsername());

        //用户不存在或密码不匹配则登录失败
        String password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        if (null == user || !user.getPassword().equals(password)) {
            log.info("【登录】失败，userName:{}", dto.getUsername());
            return new Result(false, MessageConst.LOGIN_FAIL);
        }

        //模拟用户登录成功
        log.info("【登录】成功，userName:{}", user.getUsername());
        return new Result(true, MessageConst.LOGIN_SUCCESS);
    }
}
