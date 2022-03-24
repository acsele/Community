package com.cgc.mapper;

import com.cgc.dao.DiscussPostMapper;
import com.cgc.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class DiscussPostMapperTest {
    @Resource(name = "discussPostMapper")
    private DiscussPostMapper discussPostMapper;

    @Test
    public void selectDiscussPostsTest() {
        for (DiscussPost discussPost : discussPostMapper.selectDiscussPosts(101, 2, 1)) {
            System.out.println(discussPost);
        }
    }
    @Test
    public void selectDiscussPostRows () {
        System.out.println(discussPostMapper.selectDiscussPostRows(101));
    }

    }
