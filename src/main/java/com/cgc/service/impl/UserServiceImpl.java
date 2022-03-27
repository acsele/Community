package com.cgc.service.impl;

import com.cgc.dao.LoginTicketMapper;
import com.cgc.dao.UserMapper;
import com.cgc.entity.LoginTicket;
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
import java.util.*;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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
        User user = findUserById(userId);
        System.out.println(user.getStatus());
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(1, userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    //登录成功返回登录凭证，登录失败返回登录失败信息，所有信息都放在map中，以key，value的形式记录
    @Override
    public Map<String, Object> login(String username, String password, int expiredTime) {

        Map<String, Object> map = new HashMap<String, Object>();

        //分析登录业务可能有哪些情况
        //  空值、账号不存在、账号存在但未激活、密码错误

        if (StringUtils.isEmpty(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isEmpty(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        User user = userMapper.selectUserByName(username);
        if (user == null) {
            map.put("usernameMsg", "账号不存在");
            return map;
        } else if (user.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活");
            return map;
        }

        //由于数据库中存储的是通过md5加密的数据，所以要先把用户输入的数据通过同样的方式加密之后再比较
        if (!user.getPassword().equals(CommunityUtil.md5(password + user.getSalt()))) {
            map.put("passwordMsg", "密码错误");
            return map;
        }


        //如果上面的判断都通过了，说明存在用户名和密码组合，此时生成登录凭证（凭证是一个随机生成的字符串）
        LoginTicket loginTicket = new LoginTicket(user.getId()
                , CommunityUtil.genUUID()
                , 0
                , new Date(System.currentTimeMillis() + expiredTime * 1000));
        loginTicketMapper.insert(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }
}
