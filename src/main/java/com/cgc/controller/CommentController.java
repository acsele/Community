package com.cgc.controller;

import com.cgc.entity.Comment;
import com.cgc.service.CommentService;
import com.cgc.service.DiscussPostService;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping("/add/{discussPostId}")
    public String addComment(@PathVariable int discussPostId, Comment comment) {
        //构造需要存入的comment对象
        comment.setUserId(hostHolder.getUser().getId());
        if (comment.getTargetId() == null) {
            comment.setTargetId(0);
        }
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        //调用service方法将构造的对象存入数据库
        commentService.addComment(comment);

        //如果是帖子的评论（不是回复），更新discussPost表中的评论数量
        if(comment.getEntityType()==1){
            discussPostService.updateCommentCount(comment.getEntityId());
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
