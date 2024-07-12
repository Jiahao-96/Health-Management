package com.itheima.health.aop;

import cn.hutool.jwt.Claims;
import com.alibaba.fastjson.JSONObject;
import com.itheima.health.dao.OperateLogDao;
import com.itheima.health.pojo.dto.LoginDTO;
import com.itheima.health.pojo.entity.OperateLog;
import com.itheima.health.pojo.entity.User;
import com.itheima.health.pojo.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspect {
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private OperateLogDao operateLogDao;
    @Around("@annotation(com.itheima.health.anno.LogInfo)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {

        //调用原始目标方法运行
        long begin = System.currentTimeMillis();
        Result result = (Result) joinPoint.proceed();
        long end = System.currentTimeMillis();

        //操作耗时
        Long costTime = (end - begin);

        //方法返回值
        String returnValue = JSONObject.toJSONString(result);

        String username = null;
        Integer id = null;
        if(result.getFlag()){//登录成功
            //操作人ID - 当前登录人
            //获取session，获取登录人
            HttpSession session = httpServletRequest.getSession();
            User user = (User) session.getAttribute("user");
             username = user.getUsername();
             id = user.getId();
        }else{//登录失败
            //操作方法参数
            Object[] args = joinPoint.getArgs();
            LoginDTO arg = (LoginDTO)args[1];
            username = arg.getUsername();
            id = 0;
        }

        //操作时间
        LocalDateTime operateTime = LocalDateTime.now();

        //操作类名
        String className = joinPoint.getTarget().getClass().getName();

        //操作方法名
        String methodName = joinPoint.getSignature().getName();

        //操作方法参数
        Object[] args = joinPoint.getArgs();
        String methodParams = Arrays.toString(args);

        //记录操作日志
        OperateLog operateLog = new OperateLog(null,id,username,operateTime,className,methodName,methodParams,returnValue,costTime);
        operateLogDao.insert(operateLog);
        log.info("AOP记录操作日志: {}" , operateLog);

        return result;

    }
}