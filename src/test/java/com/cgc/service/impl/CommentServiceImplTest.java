package com.cgc.service.impl;

import com.cgc.entity.Comment;
import com.cgc.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;
    @Test
    void addComment() {

        commentService.addComment(new Comment());
    }
}