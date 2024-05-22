package com.itheima.health.security;

import com.alibaba.fastjson.JSON;
import com.itheima.health.entity.Result;
import com.itheima.health.pojo.Permission;
import com.itheima.health.pojo.Role;
import com.itheima.health.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ：sean
 * @date ：Created in 2022/6/3
 * @description ：
 * @version: 1.0
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
public class HealthSecurityFilter implements Filter {
    // 定义不需要登录，就可以访问的URI
    private String[] unLoginAccessUrlList = new String[]{
            "/user/login",
            "/user/logout",
            "/common/**",
            "/mobile/**"    // 移动端的无需登录都可以访问接口
    };

    // 定义登录后，需要授权访问的URI，（没在此map的，登录即可访问）
    private Map<String, String> loginAuthUrlMap = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 读取资源需要的权限
        initAuthUrlMap();
    }

    // 模拟读取哪些路径需要哪些权限可以访问的列表
    private void initAuthUrlMap() {
        // 未在列表中的，默认用户登录就可以访问
        // 在列表中的，需要判断当前用户是否有匹配的权限，才可以访问
        // 1.比如以下URI，需要用户有CHECKITEM_DELETE权限
        loginAuthUrlMap.put("/checkitem/delete", "CHECKITEM_DELETE");
        loginAuthUrlMap.put("/checkgroup/delete", "CHECKITEM_DELETE");
        loginAuthUrlMap.put("/setmeal/delete", "CHECKITEM_DELETE");

        // 2.比如访问report下的所有内容，需要用户有REPORT_VIEW权限，才可以访问
        loginAuthUrlMap.put("/report/**", "REPORT_VIEW");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //强转对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1.获取URI
        String uri = request.getRequestURI();
        log.info("[权限检查过滤器...],当前uri:{}", uri);

        // 2.判断URI是否可以无需登录，即可访问
        boolean isNoLoginAccess = checkURI(uri);
        if (isNoLoginAccess) {
            // 3.不需要登录，直接放行
            log.info("[权限检查过滤器],允许放行");
            filterChain.doFilter(request, response);
            return;
        }

        // 4.如果登录，判断是否登录过
        if (request.getSession().getAttribute("user") != null) {
            User user = (User) request.getSession().getAttribute("user");
            log.info("[权限检查过滤器],已登录，检查当前用户是否需要授权访问，用户信息:{}", user.getUsername());

            // 检查当前用户是否对Uri，是否需要授权访问权限
            boolean isLoginAuth = checkLoginAuthURI(uri, user);
            if (isLoginAuth) {
                filterChain.doFilter(request, response);
            } else {
                // 已登录过，但是访问的资源没有权限，前端处理错误信息显示
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(new Result(false, "无操作权限")));
            }
        } else if (request.getSession().getAttribute("member") != null) {
            log.info("[权限检查过滤器],移动端用户登录，允许放行");
            filterChain.doFilter(request, response);
        } else {
            // 5.如果未登录，返回未登录的错误信息，前端处理错误信息显示
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new Result(false, "请重新登录")));
        }
    }

    // 判断是否不需要登录，可以访问的资源
    private boolean checkURI(String reqUri) {
        // 这个类，可以匹配通配符
        AntPathMatcher pathMatcher = new AntPathMatcher();
        // 这里面处理逻辑
        // 遍历数组，让数组的每个元素与当前的uri比对
        // /backend/index.html
        for (String url : unLoginAccessUrlList) {
            if (pathMatcher.match(url, reqUri)) {
                log.info("[权限检查过滤器],匹配放行的url:{}", url);
                return true;
            }
        }
        return false;
    }

    // 判断当前的资源， 是否需要登录授权
    private boolean checkLoginAuthURI(String reqUri, User user) {
        // 这个类，可以匹配通配符
        AntPathMatcher pathMatcher = new AntPathMatcher();
        // 基本思路：
        // 1. 先匹配Key，再通过key，获取这个路径需要的权限,如果没有匹配，默认可以直接访问
        // 2. 获取用户的权限列表，判断是否与当前的资源匹配，匹配即有授权，不匹配，即未授权
        Set<String> keySets = loginAuthUrlMap.keySet();
        String currKeyUrl = null;
        for (String urLKey : keySets) {
            if (pathMatcher.match(urLKey, reqUri)) {
                currKeyUrl = urLKey;
            }
        }
        if (currKeyUrl == null) {
            // 没有匹配，即登录后，无需授权访问，可以直接访问
            log.info("[权限检查过滤器],当前用户访问的URI，无需权限，直接访问，reqUri：{}", reqUri);
            return true;
        }
        String authKeyword = loginAuthUrlMap.get(currKeyUrl);
        log.info("[权限检查过滤器],当前用户访问的uri需要授权，currKeyUrl:{},keyWord:{}", currKeyUrl, authKeyword);
        log.info("[权限检查过滤器],当前用户信息：user:{}", user);

        // 判断当前用户是否拥有此权限
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            Set<Permission> permissions = role.getPermissions();
            for (Permission p : permissions) {
                if (p.getKeyword().equals(authKeyword)) {
                    log.info("[权限检查过滤器],当前用户匹配权限，允许方式，通过");
                    return true;

                }
            }
        }
        log.info("[权限检查过滤器],当前用户匹配权限不匹配，未通过");
        return false;
    }
}