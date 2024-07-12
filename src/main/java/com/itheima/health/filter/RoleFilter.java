package com.itheima.health.filter;
import com.alibaba.fastjson.JSON;
import com.itheima.health.pojo.entity.User;
import com.itheima.health.pojo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
@Slf4j
@WebFilter(urlPatterns = "/*")
public class RoleFilter implements Filter {
    @Autowired
    RedisTemplate redisTemplate ;
    @Override
    public void doFilter(ServletRequest rq, ServletResponse rp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)rq;
        HttpServletResponse response = (HttpServletResponse) rp;

        if(request.getRequestURL().toString().contains("login")){
            log.info("登录操作，直接放行");
            chain.doFilter(request,response);
            return;
        }
        if(request.getRequestURL().toString().contains("logout")){
            log.info("退出登录操作，直接放行");
            chain.doFilter(request,response);
            return;
        }
        if(request.getRequestURL().toString().contains("mobile")){
            log.info("手机端，直接放行");
            chain.doFilter(request,response);
            return;
        }

        //不是登录请求，获取用户权限信息
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if(user != null){
            Set paths = redisTemplate.opsForSet().members(user.getId() + "paths");
            //获取路径URI/setmeal/findPage
            String[] split = request.getRequestURI().toString().split("/");
            String path = split[1];
            if (paths.contains(path)){
                log.info(user.getUsername()+"有权限访问"+path+"路径，直接放行");
                chain.doFilter(request,response);
            }else {
                log.info(user.getUsername()+"没有权限访问"+path+"路径，不能放行");
                Result result = new Result(false,"没有权限访问");
                String jsonString = JSON.toJSONString(result);
                response.setContentType("text/json;charset=utf-8");
                response.getWriter().write(jsonString);

            }
        }else{
            //没登录,重定向到登录界面
            Result result = new Result(false,"没有登录，请跳转到登录界面：\"http://localhost:18080/pages/login.html\"");
            String jsonString = JSON.toJSONString(result);
            response.setContentType("text/json;charset=utf-8");
            response.getWriter().write(jsonString);
        }
    }


}