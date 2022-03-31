package com.cgc.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentMapperTest {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    void selectCommentsById() {
        System.out.println(commentMapper.selectCommentsById(1,228,10,0));
    }

    @Test
    void selectCommentCount() {
        System.out.println(commentMapper.selectCommentCount(228));
    }
}