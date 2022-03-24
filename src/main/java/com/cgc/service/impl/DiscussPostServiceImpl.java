package com.cgc.service.impl;

import com.cgc.dao.DiscussPostMapper;
import com.cgc.entity.DiscussPost;
import com.cgc.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Resource(name = "discussPostMapper")
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int limit, int offset) {
        return discussPostMapper.selectDiscussPosts(userId,limit,offset);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
