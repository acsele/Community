package com.cgc.config;

import com.cgc.controller.Interceptor.LoginInterceptor;
import com.cgc.controller.Interceptor.LoginRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/css/**", "/js/**", "/img/**");
        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/css/**", "/js/**", "/img/**");
    }

}
