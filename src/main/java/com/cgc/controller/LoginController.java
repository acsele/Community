package com.cgc.controller;

import com.cgc.entity.User;
import com.cgc.service.impl.UserServiceImpl;
import com.cgc.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserServiceImpl userServiceImpl;

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
        System.out.println("hello");
        int result = userServiceImpl.activation(userId, code);
        System.out.println(result);
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
}
