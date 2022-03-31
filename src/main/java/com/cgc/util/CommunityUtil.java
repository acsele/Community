package com.cgc.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;
import org.thymeleaf.util.StringUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String genUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //md5加密
    public static String md5(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //使用fastJSON工具，把ajax请求中用于返回给前端的数据(ajax请求状态码，提示信息，数据）封装成JSON，再转化成字符串
    public static String genJson(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }
    //生成JSON字符串方法的重载方法（适应不同个数的参数）
    public static String genJson(int code, String msg) {
        return genJson(code, msg, null);
    }
    public static String genJson(int code) {
        return genJson(code, null, null);
    }

}
