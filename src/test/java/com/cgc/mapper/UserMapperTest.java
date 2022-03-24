package com.cgc.mapper;

import com.cgc.dao.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void selectUserByIdTest() {
        System.out.println(userMapper.selectUserById(112));
    }

    @Test
    public void selectUserByNameTest() {
        System.out.println(userMapper.selectUserByName("bbb"));
    }
}
