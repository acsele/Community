package com.cgc.dao;

import com.cgc.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    @Insert({"insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"})
    @Options(useGeneratedKeys = true, keyColumn = "id")
    int insert(LoginTicket loginTicket);


    @Select({"select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    //if标签用于演示动态sql的写法，在这里没有用
    @Update({"<script>",
            "update login_ticket set status=#{status} ",
            "where ticket=#{ticket}",
            "<if test=\"ticket !=null\">",
            "and 1=1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
