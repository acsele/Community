package com.cgc.service.impl;

import com.cgc.service.LikeService;
import com.cgc.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 当用户点击点赞按钮时，执行该方法（可以为帖子、评论、回复点赞）
     *
     * @param entityType 0表示帖子、1表示评论、2表示回复
     * @param entityId   帖子、评论、或者回复的id
     * @param userId     点赞的人的userId
     */
    @Override
    public void like(int entityType, int entityId, int userId, int entityUserId) {
        //这个key用于存储被点赞帖子或评论的获赞列表
        String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
        //这个key用于存储被点赞人的获赞列表
        String userLikeKey = RedisKeyUtil.genUserLikeKey(entityUserId);

        //当用户点击点赞按钮时，有两种情况：用户之前已经为该内容点过赞，此时应该取消赞。之前没有点过赞，现在点赞
        //另外，当一个用户为一个帖子或评论点赞时，需要同时修改两个数据：帖子本身的获赞赞+1，帖子作者的获赞+1
        //这两个应该是同时成功或者同时失败，所以要使用redis事务

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

                //标志事务开始
                operations.multi();
                if (isMember) {
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                } else {
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }

                //标志事务结束
                return operations.exec();
            }
        });
    }

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.genUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
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
