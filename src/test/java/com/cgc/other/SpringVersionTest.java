package com.cgc.other;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;

@SpringBootTest
public class SpringVersionTest{

    @Test
    public void test01(){
        System.out.println("SpringVersion"+SpringVersion.getVersion());
        System.out.println("SpringBootVersion"+SpringBootVersion.getVersion());
    }

}
