package com.cgc.controller;

import com.cgc.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class TestController {

    @RequestMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse httpServletResponse) {
        //创建cookie
        Cookie cookie = new Cookie("userId", CommunityUtil.genUUID());
        //设置cooKie生效路径（访问个路径时使用该cookie）
        cookie.setPath("/community/get");
        //设置cookie生存时间
        cookie.setMaxAge(10 * 60);
        httpServletResponse.addCookie(cookie);
        return "set test";
    }


    //通过上面为浏览器设置的该路径的cookie，当访问该路径时，请求头中会携带cookie
    @RequestMapping("/community/testCookie")
    public String test(Model model) {
        model.addAttribute("responseMsg", "cookie has been set");
        return "site/test";
    }


    @RequestMapping(value = "/community/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("userId") String userId) {
        System.out.println(userId);
        return "get Cookie";
    }

    //测试session

    //设置session
    @RequestMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "set Session";
    }

    //获取session
    @RequestMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession httpSession){
        System.out.println(httpSession.getAttribute("id"));
        System.out.println(httpSession.getAttribute("name"));
        return "get Session";
    }
}
