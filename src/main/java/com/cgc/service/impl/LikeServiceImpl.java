package com.cgc.service.impl;

import com.cgc.service.LikeService;
import com.cgc.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 当用户点击点赞按钮时，执行该方法（可以为帖子、评论、回复点赞）
     *
     * @param entityType   0表示帖子、1表示评论、2表示回复
     * @param entityId     帖子、评论、或者回复的id
     * @param userId       点赞的人的userId
     */
    @Override
    public void like(int entityType, int entityId, int userId) {
        //当用户点击点赞按钮时，有两种情况：用户之前已经为该内容点过赞，此时应该取消赞。之前没有点过赞，现在点赞
        String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

        if (isMember) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }

    }

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }


    /**
     * 主要用于页面显示，当用户已经点赞时，显示已赞，未点赞时，只显示赞。
     *
     * @param userId     当前登录的用户id
     * @param entityType 实体类型（评论、帖子、回复）
     * @param entityId   实体id
     * @return 为点赞时返回0，点过赞时返回1
     */
    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
