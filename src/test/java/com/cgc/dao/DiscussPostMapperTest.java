package com.cgc.dao;

import com.cgc.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiscussPostMapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    void insertDiscussPost() {
        DiscussPost discussPost=new DiscussPost();
        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Test
    void selectDiscussPostById() {
        System.out.println(discussPostMapper.selectDiscussPostById(272));
    }

    @Test
    void updateDiscussPost() {
        DiscussPost discussPost=discussPostMapper.selectDiscussPostById(217);
        discussPost.setTitle("就业信息");
        System.out.println(discussPostMapper.updateDiscussPost(discussPost));
    }
}