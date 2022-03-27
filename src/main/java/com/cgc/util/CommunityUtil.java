package com.cgc.util;

import org.springframework.util.DigestUtils;
import org.thymeleaf.util.StringUtils;

import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String genUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //md5加密
    public static String md5(String key){
        if(StringUtils.isEmpty(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
