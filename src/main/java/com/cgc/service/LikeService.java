package com.cgc.service;

/**
 * 可以被点赞的对象：帖子、评论、评论的回复（这些都统一用形参entity表示）
 */
public interface LikeService {

    /**
     * 当用户点击点赞按钮时，执行该方法（可以为帖子、评论、回复点赞）
     *
     * @param entityType   0表示帖子、1表示评论、2表示回复
     * @param entityId     帖子、评论、或者回复的id
     * @param userId       点赞的人的userId
     * @param entityUserId 被点赞人的userId
     */
    void like(int entityType, int entityId, int userId, int entityUserId);


    long findEntityLikeCount(int entityType, int entityId);

    int findUserLikeCount(int userId);

    /**
     * 主要用于页面显示，当用户已经点赞时，显示已赞，未点赞时，只显示赞。
     *
     * @param userId     当前登录的用户id
     * @param entityType 实体类型（评论、帖子、回复）
     * @param entityId   实体id
     * @return 为点赞时返回0，点过赞时返回1
     */
    int findEntityLikeStatus(int userId, int entityType, int entityId);
}
