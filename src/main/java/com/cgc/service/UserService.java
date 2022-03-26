package com.cgc.service;

import com.cgc.entity.User;

import java.util.Map;

public interface UserService {

    User findUserById(int id);

    User findUserByName(String name);

    Map<String, Object> register(User user);

    Integer activation(int userId,String code);
}
