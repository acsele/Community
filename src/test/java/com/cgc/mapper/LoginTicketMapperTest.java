package com.cgc.mapper;

import com.cgc.dao.LoginTicketMapper;
import com.cgc.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class LoginTicketMapperTest {

    @Autowired
    private LoginTicketMapper loginTicketMapper;


    @Test
    public void selectTest() {
        System.out.println(loginTicketMapper.selectByTicket("ddd"));
    }

    @Test
    public void insertTest() {
        loginTicketMapper.insert(new LoginTicket(100,"ccc",0,new Date()));
    }
    @Test
    public void updateTest(){
        loginTicketMapper.updateStatus("ddd",2);
    }

}
