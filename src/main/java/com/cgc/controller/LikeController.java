package com.cgc.controller;

import com.cgc.entity.Event;
import com.cgc.entity.User;
import com.cgc.event.EventProducer;
import com.cgc.service.LikeService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.CommunityUtil;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();

        //点赞
        likeService.like(entityType, entityId, user.getId(), entityUserId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //获取当前点赞状态（虽然在进行点赞操作之前已经进行了判断，但在页面显示的时候依然不知道当前的点赞状态）
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        //系统通知：点赞业务完成，系统通知被点赞用户
        if (likeStatus == 1) {
            Event event = new Event()
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setTopic(TOPIC_Like)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.releaseEvent(event);
        }

        return CommunityUtil.genJson(0, null, map);

    }
}
