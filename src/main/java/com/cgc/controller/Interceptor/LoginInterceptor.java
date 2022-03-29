package com.cgc.controller.Interceptor;

import com.cgc.entity.LoginTicket;
import com.cgc.entity.User;
import com.cgc.service.impl.UserServiceImpl;
import com.cgc.util.CookieUtil;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HostHolder hostHolder;

    //preHandle见名知意，是在controller方法执行之前执行的，一般用于登录验证
    //拦截器做登录验证时一般有以下步骤：
    //  1. 从请求的cookie中获取登录凭证
    //  2. 使用这个凭证从数据库中获取信息
    //  3. 根据获取到的信息情况，判断是否登录成功
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取登录凭证（因为cookie中的信息很多，所以每次从cookie中获取信息都要遍历cookie。不如写个根据key获取cookie值的工具类
        String ticket = CookieUtil.getValue(request, "ticket");
        System.out.println(ticket);
        if (ticket != null) {
            //查询数据库中是否有该ticket
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0) {
                //有该凭证且凭证有效，此时查询凭证对应的用户信息，先保存起来。
                //后面的controller和service处理完相关业务后会返回给前端一个ModelAndView，在postHandel中将user添加到ModelAndview中
                //如何保存：因为服务器会处理多个用户的请求，所以不同请求得到的user对象不同，希望存储该对象时，能和请求关联起来
                //使用ThreadLocal类，把处理请求的线程作为key，需要存储的数据作为value（这里又对ThreadLocal做了一层封装HostHolder）
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        hostHolder.clear();
    }
}
