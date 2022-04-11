package com.cgc.service;

import com.cgc.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsById(int type,int id,int limit,int offset);

    Comment findCommentById(int commentId);

    int findCommentCount(int id);

    int addComment(Comment comment);
}
