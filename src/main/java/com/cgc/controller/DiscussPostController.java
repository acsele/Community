package com.cgc.controller;

import com.cgc.entity.Comment;
import com.cgc.entity.DiscussPost;
import com.cgc.entity.Page;
import com.cgc.entity.User;
import com.cgc.service.CommentService;
import com.cgc.service.DiscussPostService;
import com.cgc.service.LikeService;
import com.cgc.service.UserService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.CommunityUtil;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    //添加帖子
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.genJson(403, "请先登录登录");
        }
        DiscussPost discussPost = new DiscussPost(user.getId(), title, content, 0, 0, new Date(), 0, 0);
        discussPostService.addDiscussPost(discussPost);
        return CommunityUtil.genJson(200, "发布成功！");
    }

    //显示帖子详情，包括显示所有评论及评论回复
    @RequestMapping("/detail/{discussPostId}")
    public String detailsOfDiscussPost(@PathVariable Integer discussPostId, Model model, Page page) {
        //查询需要返回给前端的用户信息，文章信息
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("post", discussPost);
        model.addAttribute("likeCount",likeService.findEntityLikeCount(1,discussPostId));


        //设置评论分页
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());

        //处理该帖子的评论信息（两层循环，先处理直接对帖子的评论，再处理对这些评论的回复）
        //下面的代码中xxxList用于暂存从数据库查到的数据，xxxMaps用于暂存准备发送给前端的数据
        List<Map<String, Object>> commentMaps = new ArrayList<>(); // 用于存放该文章的所有评论

        List<Comment> commentList = commentService.findCommentsById(1, discussPostId, page.getLimit(), page.getOffset());
        for (Comment comment : commentList) {
            Map<String, Object> commentMap = new HashMap<>(); //每一条评论的展示模块应该包含哪些信息（评论内容，发评论者，回复）
            //先处理该评论的回复信息
            List<Map<String, Object>> replyMaps = new ArrayList<>(); //存放当前评论的所有回复

            List<Comment> replyList = commentService.findCommentsById(2, comment.getId(), Integer.MAX_VALUE, 0);
            for (Comment reply : replyList) {
                Map<String, Object> replyMap = new HashMap<>(); //每一条回复的展示模块应该展示哪些信息（回复内容，谁回复的，回复谁的）
                replyMap.put("reply", reply);
                replyMap.put("replyFrom", userService.findUserById(reply.getUserId()));
                replyMap.put("replyTo", reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId()));
                replyMap.put("replyLikeCount",likeService.findEntityLikeCount(2,reply.getId()));

                replyMaps.add(replyMap);
            }
            commentMap.put("replys", replyMaps);
            commentMap.put("replyCount", replyList.size());
            commentMap.put("commentLikeCount",likeService.findEntityLikeCount(2,comment.getId()));
            commentMap.put("comment", comment);
            commentMap.put("commentFrom", userService.findUserById(comment.getUserId()));

            commentMaps.add(commentMap);
        }
        model.addAttribute("comments", commentMaps);
        return "/site/discuss-detail";
    }

}
