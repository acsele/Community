package com.cgc.dao;

import com.cgc.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectUserById(int id);

    User selectUserByName(String name);

}
