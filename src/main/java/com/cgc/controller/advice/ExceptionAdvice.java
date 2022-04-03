package com.cgc.controller.advice;

import com.cgc.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 自定义异常处理（主要是系统自带的异常处理只能处理普通请求，当前端是异步请求时导致的异常无法处理）
     *
     * @param e        接收项目中抛出的异常
     * @param request  从前端的请求中获取信息，比如请求方式
     * @param response 向前端返回信息时使用（如果是异步请求，需要用response向前端返回JSON字符串）
     * @throws IOException 抛出从response获取输出流对象时的异常
     */
    @ExceptionHandler(Exception.class) //括号中表示要处理哪些异常
    //当发生异常时，用形参接收异常。后面两个request和response对象用于将异常信息显示给前端
    public void exceptionHandle(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {

        //将异常信息记录到日志文件
        logger.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        //把异常信息同时返回给前端页面。此时有两种情况：普通请求、异步请求。如何判断是哪种请求：request对象的属性
        String xRequestedWith = request.getHeader("x-requested-with");

        //如果是异步请求，返回带有错误信息的json字符串
        if (xRequestedWith != null && xRequestedWith.equals("XMLHttpRequest")) {
            response.setContentType("application/plain;charset=utf-8");  //plain表示这是一个普通字符串，需要前端使用jquery的$.parse()进行解析
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.genJson(1, "服务器异常"));
        } else {
            //如果是普通请求，重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/myerror");
        }


    }

}
