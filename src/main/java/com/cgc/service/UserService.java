package com.cgc.service;

import com.cgc.entity.User;

public interface UserService {

    User findUserById(int id);

    User findUserByName(String name);

}
