package com.cgc.controller;

import com.cgc.entity.Event;
import com.cgc.entity.Page;
import com.cgc.entity.User;
import com.cgc.event.EventProducer;
import com.cgc.service.FollowService;
import com.cgc.service.UserService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.CommunityUtil;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 用户点击关注或者取消关注
     *
     * @param entityType 关注关系中的客体类型
     * @param entityId   关注关系中的客体id
     * @return 操作结果信息
     */
    @RequestMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        //完成关注的数据相关操作
        followService.follow(user.getId(), entityType, entityId);

        //系统通知：关注业务完成，通过系统通知被关注用户
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.releaseEvent(event);

        return CommunityUtil.genJson(0, "操作成功");
    }

    /**
     * 已关注列表
     *
     * @param userId 关注关系中的主体id
     * @param page   支持分页
     * @param model  返回给前端的数据（主要包括两部分：当前用户信息，当前用户所有已关注者列表集合）
     * @return 模板文件
     */
    @RequestMapping("/followList/{userId}")
    public String followListPage(@PathVariable int userId, Page page, Model model) {

        page.setRows((int) followService.findFollowCount(userId, ENTITY_TYPE_USER));
        page.setLimit(5);
        page.setPath("/followList/" + userId);

        model.addAttribute("user", userService.findUserById(userId));
        model.addAttribute("loginUser", hostHolder.getUser());
        model.addAttribute("followList", followService.findFollowList(userId, page.getLimit(), page.getOffset()));

        return "/site/followee";
    }

    /**
     * 粉丝列表
     *
     * @param userId 当前用户id
     * @param page   支持分页
     * @param model  返回给前端的数据（该主页用户对象，粉丝列表）
     * @return 粉丝列表模板文件
     */
    @RequestMapping("/fansList/{userId}")
    public String fansListPage(@PathVariable int userId, Page page, Model model) {
        page.setRows((int) followService.findFollowCount(userId, ENTITY_TYPE_USER));
        page.setLimit(5);
        page.setPath("/fansList/" + userId);

        model.addAttribute("user", userService.findUserById(userId));
        model.addAttribute("loginUser", hostHolder.getUser());
        model.addAttribute("fansList", followService.findFansList(userId, page.getLimit(), page.getOffset()));

        System.out.println(followService.findFansList(userId, page.getLimit(), page.getOffset()));

        return "/site/follower";
    }


}
