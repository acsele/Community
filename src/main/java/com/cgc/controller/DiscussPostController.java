package com.cgc.controller;

import com.cgc.dao.DiscussPostMapper;
import com.cgc.entity.DiscussPost;
import com.cgc.entity.User;
import com.cgc.service.DiscussPostService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.CommunityUtil;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user=hostHolder.getUser();
        if(user==null){
            return CommunityUtil.genJson(403,"请先登录登录");
        }
        DiscussPost discussPost = new DiscussPost(user.getId(), title, content, 0, 0, new Date(), 0, 0);
        discussPostService.addDiscussPost(discussPost);
        return CommunityUtil.genJson(200,"发布成功！");
    }
}
