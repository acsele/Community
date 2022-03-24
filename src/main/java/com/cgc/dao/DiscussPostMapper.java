package com.cgc.dao;

import com.cgc.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * 查询用户的帖子
     *
     * @param userId 用户id
     * @param limit  每页显示条数
     * @param offset 从第几页开始输出
     * @return 该用户所有帖子Java对象的list集合
     */
    List<DiscussPost> selectDiscussPosts(int userId, int limit, int offset);

    /**
     * 查询用户帖子条数
     *
     * @param userId 用户id
     * @return 该用户帖子条数
     */
    int selectDiscussPostRows(int userId);

}
