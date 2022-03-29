package com.cgc.controller;

import com.cgc.dao.LoginTicketMapper;
import com.cgc.entity.User;
import com.cgc.service.impl.UserServiceImpl;
import com.cgc.util.CommunityConstant;
import com.google.code.kaptcha.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }


    /**
     * controller层主要处理和视图相关的逻辑，也就是你让我注册，我把注册的具体实现交给业务层，业务层执行完了告诉我注册成功了还是失败了
     * （如何告知我：这里是通过一个map集合，map集合为空表示成功，不为空时，包含的是失败的相关信息），
     * 我唯一需要做的就是，规定成功时应该跳转到哪个页面，显示什么数据，失败时应该跳转到哪个页面，显示什么数据
     *
     * @param model 封装需要在页面上显示的信息
     * @param user  用对象从页面接收信息（接收请求或者模板中数据的方式有两种：对象接收、逐个接收），
     *              对象接收时：对象中的属性名和页面中的变量名必须相同，
     *              逐个接收时： 如果用于接收数据的形参名与页面中的变量名不同，需要使用@PathVarialbe()指定形参对应页面中的哪个变量
     * @return 注册成功时返回一个操作成功页面，并显示操作成功信息；注册失败时，返回注册页面，并显示错误信息
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userServiceImpl.register(user);

        //注册成功(首先跳转到操作结果页面，提示已经操作成功，之后自动（或手动点击）跳转到首页
        //所以返回给前端的model中应该添加两个信息：操作成功提示、需要自动跳转到的页面地址
        if (map.isEmpty()) {
            model.addAttribute("msg", "注册成功");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else { //注册失败还是跳转到当前页面，只不过要显示失败的相关信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }

    }


    /**
     * 处理激活邮件中的激活链接，调用业务层的激活方法，业务层告诉我激活的结果，可能成功、失败、或者重复激活
     *
     * @param model  把激活结果信息包装在model中返回给前端页面
     * @param userId 激活链接中获取的userId
     * @param code   激活链接中获取的激活码
     * @return 激活结果页面
     */

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userServiceImpl.activation(userId, code);
        switch (result) {
            case ACTIVATION_SUCCESS:
                model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
                model.addAttribute("target", "/login");
                break;
            case ACTIVATION_REPEAT:
                model.addAttribute("msg", "重复操作,您的账号已经激活过了!");
                model.addAttribute("target", "/index");
                break;
            default:
                model.addAttribute("msg", "激活失败,请检查您的激活码是否正确!");
                model.addAttribute("target", "/index");
                break;
        }
        return "/site/operate-result";
    }


    //生成验证码
    @RequestMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        //生成验证码（分两步：生成随机文本，根据文本生成图片验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha", text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("验证码输出失败: " + e.getMessage());
        }

    }


    //登录方法
    @RequestMapping("/login")
    public String showLoginPage() {
        return "/site/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe
            , Model model, HttpSession session, HttpServletResponse response) {
        //检查验证码(把用户输入的验证码和session中存放的验证码比较）
        String kaptcha = (String) session.getAttribute("kaptcha");
        System.out.println(kaptcha+code);
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(kaptcha) || !code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码错误");
            return "/site/login";
        }
        //检查账号密码
        // 如果返回值map中有ticket，说明登录成功，此时设置cookie，
        // 下次访问时，如果在请求头中带有这个cookie，就不需要再登录，直接进入首页
        // 否则，如果map中没有ticket，要展示失败信息
        int expiredTime = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map map = userServiceImpl.login(username, password, expiredTime);

        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredTime);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }

    }

    //退出登录
    @RequestMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userServiceImpl.logout(ticket);
        return "redirect:/login";
    }
}
