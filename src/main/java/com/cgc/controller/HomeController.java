package com.cgc.controller;

import com.cgc.entity.DiscussPost;
import com.cgc.entity.Page;
import com.cgc.service.DiscussPostService;
import com.cgc.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Resource(name = "discussPostServiceImpl")
    private DiscussPostService discussPostService;

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page) {

        //分页显示
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");


        //每一个帖子的前端模块显示的信息包括两部分：帖子对象、帖子对应的用户对象
        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0, page.getLimit(), page.getOffset());
        List<Map<String, Object>> discussPosts = new ArrayList<Map<String, Object>>();
        if (discussPostList != null) {
            for (DiscussPost discussPost : discussPostList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("post", discussPost);
                map.put("user", userService.findUserById(discussPost.getUserId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "index";
    }

}
