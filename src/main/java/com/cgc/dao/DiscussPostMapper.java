package com.cgc.dao;

import com.cgc.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //查询用户的帖子
    List<DiscussPost> selectDiscussPosts(int userId, int limit, int offset);

    //查询用户帖子条数
    int selectDiscussPostRows(int userId);

    //新增帖子
    int insertDiscussPost(DiscussPost discussPost);

    //查询帖子
    DiscussPost selectDiscussPostById(int id);

    //更新帖子评论数
    int updateDiscussPost(DiscussPost discussPost);
}
