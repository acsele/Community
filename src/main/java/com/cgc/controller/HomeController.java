package com.cgc.controller;

import com.cgc.entity.DiscussPost;
import com.cgc.entity.Page;
import com.cgc.entity.User;
import com.cgc.service.DiscussPostService;
import com.cgc.service.LikeService;
import com.cgc.service.MessageService;
import com.cgc.service.UserService;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LikeService likeService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping({"/index", "/"})
    public String getIndexPage(Model model, Page page) {
        User user = hostHolder.getUser();

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
                map.put("likeCount", likeService.findEntityLikeCount(1, discussPost.getId()));
                map.put("commentCount", discussPostService.updateCommentCount(discussPost.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        if (messageService != null && user != null) {
            model.addAttribute("messageCount", messageService.findUnReadNoticeCount(user.getId(), null)
                    + messageService.findUnreadMessagesCount(user.getId(), null));
        }
        return "index";
    }


    //手动处理异常时，需要配置错误页面的额访问路径(注意这里的路径一般不要写成/error，因为spring默认实现的异常处理路径就是/error，如果
    //我们自定义的也是，就会产生冲突，如果一定要使用/error路径，就要让controller实现ErrorController接口，表示使用自定义的异常处理类
    @RequestMapping("/myerror")
    public String getErrorPage() {
        return "/error/500";
    }

}
