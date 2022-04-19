package com.cgc;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 项目最终要放到tomcat中运行，由tomcat启动，tomcat中已经有main方法，所以项目不能通过main方法启动，需要通过这种方式启动，
 * 继承springBoot提供的SpringBootServletInitializer类，重写configure方法，指定项目主配置文件的位置
 */
public class CommunityServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CommunityApplication.class);
    }
}
