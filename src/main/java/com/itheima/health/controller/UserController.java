package com.itheima.health.controller;

import com.itheima.health.common.MessageConst;
import com.itheima.health.common.PasswordMethodConst;
import com.itheima.health.pojo.result.Result;
import com.itheima.health.pojo.entity.User;
import com.itheima.health.service.UserService;
import com.itheima.health.pojo.dto.LoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
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
        String userMysqlPassword = user.getPassword();
        //截取用户密码的加密方式
        String passwordMethod = userMysqlPassword.substring(userMysqlPassword.indexOf("{"), userMysqlPassword.lastIndexOf("}")+1);
        //截取用户真正的密码
        String userPassword = userMysqlPassword.substring(userMysqlPassword.lastIndexOf("}") + 1);
        //使用指定加密方式进行密码比对
        switch (passwordMethod) {
            case PasswordMethodConst.MD5:
                //使用md5加密方式，用户不存在或密码不匹配则登录失败
                String passwordMD5 = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
                if (null == user || !userPassword.equals(passwordMD5)) {
                    log.info("【登录】失败，userName:{}", dto.getUsername());
                    return new Result(false, MessageConst.LOGIN_FAIL);
                }
                break;
            case PasswordMethodConst.BCRYPT:
//                String passwordBcrypt = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
                //直接使用Bcrypt的校验方法，使用加密前的密码和加密后的密码和进行比较就行。
                if (null == user || !BCrypt.checkpw(dto.getPassword(),userPassword)) {
                    log.info("【登录】失败，userName:{}", dto.getUsername());
                    return new Result(false, MessageConst.LOGIN_FAIL);
                }
                break;
        }
        //用户登录成功
        log.info("【登录】成功，userName:{}", user.getUsername());
        return new Result(true, MessageConst.LOGIN_SUCCESS);
    }
}
