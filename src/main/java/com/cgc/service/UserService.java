package com.cgc.service;

import com.cgc.entity.LoginTicket;
import com.cgc.entity.User;

import java.util.Map;

public interface UserService {

    User findUserById(int id);

    User findUserByName(String name);

    int updateHeaderUrl(int id, String url);

    Map<String, Object> register(User user);

    Integer activation(int userId, String code);

    Map<String, Object> login(String username, String password, int expiredTime);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);
}
