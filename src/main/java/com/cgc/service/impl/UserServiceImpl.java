package com.cgc.service.impl;

import com.cgc.dao.UserMapper;
import com.cgc.entity.User;
import com.cgc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource(name = "userMapper")
    private UserMapper userMapper;


    @Override
    public User findUserById(int id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public User findUserByName(String name) {
        return userMapper.selectUserByName(name);
    }
}
