package com.cgc.controller;

import com.cgc.entity.DiscussPost;
import com.cgc.entity.Page;
import com.cgc.service.ElasticSearchService;
import com.cgc.service.LikeService;
import com.cgc.service.UserService;
import com.cgc.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping("/search")
    public String search(String keyword, Page page, Model model) {
        List<DiscussPost> discussPostsList = elasticSearchService.search(keyword, page.getLimit(), page.getOffset());

        System.out.println(discussPostsList);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        for (DiscussPost discussPost : discussPostsList) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", discussPost);
            map.put("user", userService.findUserById(discussPost.getUserId()));
            map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));

            discussPosts.add(map);
        }

        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        //设置分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(discussPostsList.size());

        return "/site/search";
    }
}
