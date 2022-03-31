package com.cgc.service.impl;

import com.cgc.dao.DiscussPostMapper;
import com.cgc.entity.DiscussPost;
import com.cgc.service.DiscussPostService;
import com.cgc.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Resource(name = "discussPostMapper")
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int limit, int offset) {
        return discussPostMapper.selectDiscussPosts(userId,limit,offset);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {

        if(discussPost==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //转义Html标记（让浏览器不要处理HTML标签）,DiscussPost对象中可能出现不合法词的地方后标题、内容
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveWordsFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveWordsFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }
}
