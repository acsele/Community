package com.cgc;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

//@SpringBootApplication
//@MapperScan("com.cgc.dao")
//public class CommunityApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(CommunityApplication.class, args);
//    }
//
//}

@Slf4j
@SpringBootApplication
public class CommunityApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CommunityApplication.class, args);
            System.out.println("Server startup done.");
        } catch (Exception e) {
            log.error("服务启动报错", e);
        }
    }
}