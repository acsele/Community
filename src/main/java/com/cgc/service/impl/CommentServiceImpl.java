package com.cgc.service.impl;

import com.cgc.dao.CommentMapper;
import com.cgc.dao.DiscussPostMapper;
import com.cgc.entity.Comment;
import com.cgc.entity.DiscussPost;
import com.cgc.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<Comment> findCommentsById(int type, int id, int limit, int offset) {
        return commentMapper.selectCommentsById(type, id, limit, offset);
    }

    @Override
    public int findCommentCount(int id) {
        return commentMapper.selectCommentCount(id);
    }

    @Override
    public int addComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }
}
