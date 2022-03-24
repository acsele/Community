package com.cgc.service;

import com.cgc.entity.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> findDiscussPosts(int userId,int limit,int offset);

    int findDiscussPostRows(int userId);
}
