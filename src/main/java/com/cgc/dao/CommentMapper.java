package com.cgc.dao;

import com.cgc.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 查询评论
     *
     * @param type   可能是帖子的，也可能是评论的回复，所以要设置参数type，表明是查询帖子的评论还是查询回复
     * @param id     如果是查帖子的评论，需要指定查哪个帖子的评论（帖子的id），如果查回复（需要指定查哪个评论的回复（评论的id）
     *
     * 分页相关
     * @param limit  设置每次查几条数据
     * @param offset 从第几页开始查
     * @return 评论对象集合
     */
    List<Comment> selectCommentsById(int type, int id, int limit, int offset);

    Comment selectCommentById(int commentId);

    //查询总评论数，用于计算分页数(我们只对帖子的评论做分页不对回复做分页，所以查询的一定是帖子的评论数量，不需要去查回复数量）
    int selectCommentCount(int id);

    //添加评论
    int insertComment(Comment comment);

}
