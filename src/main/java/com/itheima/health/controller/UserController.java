package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.User;
import com.itheima.health.service.UserService;
import com.itheima.health.util.PasswordUtils;
import com.itheima.health.vo.LoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangmeng
 * @description
 * @date 2019/9/6
 **/
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
    public Result login(HttpServletRequest request, @RequestBody LoginParam param) {
        log.info("[登录]data:{}",param);
        //rpc调用查询用户信息
        User user = userService.findByUsername(param.getUsername());
        if (null == user ) {
            log.info("[登录]失败，用户不存在");
            return new Result(false, MessageConst.LOGIN_FAIL+",用户不存在！");
        }
        //用户不存在或密码不匹配则登录失败
        boolean pswFlag = PasswordUtils.checkPassword(param.getPassword(),user.getPassword());
        if (! pswFlag) {
            log.info("[登录]失败，密码不匹配");
            return new Result(false, MessageConst.LOGIN_FAIL+",密码不匹配！");
        }
        //模拟用户登录成功
        log.info("[登录]成功，user:{}",user.getUsername());
        request.getSession().setAttribute("user",user);
        return new Result(true, MessageConst.LOGIN_SUCCESS);
    }
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request){
        request.getSession().invalidate();
        return new Result(true, MessageConst.ACTION_SUCCESS);
    }
}
