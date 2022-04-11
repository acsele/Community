package com.cgc.controller;

import com.cgc.entity.Comment;
import com.cgc.entity.Event;
import com.cgc.event.EventProducer;
import com.cgc.service.CommentService;
import com.cgc.service.DiscussPostService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping("/add/{discussPostId}")
    public String addComment(@PathVariable int discussPostId, Comment comment) {
        //构造需要存入的comment对象
        comment.setUserId(hostHolder.getUser().getId());
        //为什么targetId字段可能为空：targetId字段主要是为了在前端显示” A 回复了 B ：“这种效果，
        // 只有在三级评论中才会用到，也就是评论的回复的回复，其他情况下targetId都为空
        if (comment.getTargetId() == null) {
            comment.setTargetId(0);
        }
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        //调用service方法将构造的对象存入数据库
        commentService.addComment(comment);

        //系统通知：评论业务完成，此时要通过系统通知，提醒被评论用户，他有新的评论
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);

        //如果是帖子的评论（不是回复），更新discussPost表中的评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            discussPostService.updateCommentCount(comment.getEntityId());
            //查询帖子的作者id存入event中
            int postAuthorId = discussPostService.findDiscussPostById(comment.getEntityId()).getUserId();
            event.setEntityUserId(postAuthorId);
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            //如果是对评论的回复，查询评论的作者id，存入event
            int commentAuthorId = commentService.findCommentById(comment.getEntityId()).getUserId();
            event.setEntityUserId(commentAuthorId);
        }
        //发布事件到kafka
        eventProducer.releaseEvent(event);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
