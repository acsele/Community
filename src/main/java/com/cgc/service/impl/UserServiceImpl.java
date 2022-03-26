package com.cgc.service.impl;

import com.cgc.dao.UserMapper;
import com.cgc.entity.User;
import com.cgc.service.UserService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.CommunityUtil;
import com.cgc.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    //项目网址
    @Value("${community.path.domain}")
    private String domain;

    //项目名
    @Value("${server.servlet.context-path}")
    private String path;


    @Override
    public User findUserById(int id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public User findUserByName(String name) {
        return userMapper.selectUserByName(name);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isEmpty(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            map.put("usernameMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            map.put("usernameMsg", "邮箱不能为空");
            return map;
        }


        //验证账号是否已存在
        User u = userMapper.selectUserByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "账号已存在");
            return map;
        }
        //验证邮箱是否已存在
        u = userMapper.selectUserByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        //所有参数都合法--》注册用户
        //对用户设置的密码做加密处理，先生成一个随机字符串，再用MD5为这个随机字符串+用户密码加密
        user.setSalt(CommunityUtil.genUUID());
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));

        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.genUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000))); //设置默认头像
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 设计url模式：http://localhost:8023/activation/101/code
        //注意这里面的user.getId(),因为我们没有为user对象的id属性赋值，所以显示的是默认值
        //mybatis提供了一个use-generated-keys属性，为true时，向数据库插入数据，在数据库中自动生成的主键值会被自动设置给
        //Java对象中与主键对应的属性，所以在插入时要在sql中指定主键对应的Java对象中的属性是哪一个（keyProperty=“id”）
        String url = domain + path + "activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation.html", context);

        mailClient.sendMail(user.getEmail(), "激活", content);

        return map;
    }

    @Override
    public Integer activation(int userId, String code) {
        User user=findUserById(userId);
        System.out.println(user.getStatus());
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(1,userId);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }
}
