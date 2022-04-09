package com.cgc.service.impl;

import com.cgc.entity.User;
import com.cgc.service.FollowService;
import com.cgc.service.UserService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.HostHolder;
import com.cgc.util.RedisKeyUtil;
import org.apache.catalina.Host;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService, CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 关注某个用户：当用户点击关注（或取消关注）按钮时都是执行该方法
     * 采用redis中的zSet数据结构，因为zSet中的每个元素都是value score，刚好对应userId  time
     * 先根据传入的参数查询是否存在，如果存在就删除，如果不存在就添加（以此通过一个方法即实现关注功能，又实现取消关注功能）
     *
     * @param followUserId         点关注的人的userId
     * @param beFollowedEntityType 被关注的实体类型
     * @param beFollowedEntityId   被关注的实体的id
     */
    @Override
    public void follow(int followUserId, int beFollowedEntityType, int beFollowedEntityId) {
        //followList是关注着的关注列表，fansList是被关注者的粉丝列表
        String followListKey = RedisKeyUtil.genFollowListKey(beFollowedEntityType, followUserId);
        String fansListKey = RedisKeyUtil.genFansListKey(beFollowedEntityType, beFollowedEntityId);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                Object isMember = redisTemplate.opsForZSet().rank(followListKey, beFollowedEntityId);

                //事务开始标志
                operations.multi();
                if (isMember == null) {
                    redisTemplate.opsForZSet().add(followListKey, beFollowedEntityId, System.currentTimeMillis()); //把被关注者加入关注着的关注列表
                    redisTemplate.opsForZSet().add(fansListKey, followUserId, System.currentTimeMillis()); //把关注者加入被关注者的粉丝列表
                } else {
                    redisTemplate.opsForZSet().remove(followListKey, beFollowedEntityId);
                    redisTemplate.opsForZSet().remove(fansListKey, followUserId);
                }
                //事务结束标志
                return operations.exec();
            }
        });


    }

    /**
     * 查询关注列表（支持分页）
     *
     * @param userId 要查询哪个用户的关注列表
     * @param limit  每页显示几个
     * @param offset 从第几个开始
     * @return 用于展示到前端关注列表中的信息集合（每一项主要包括两部分：关注着的user对象，关注的时间）
     */
    @Override
    public List<Map<String, Object>> findFollowList(int userId, int limit, int offset) {
        User loginUser = hostHolder.getUser();
        String followListKey = RedisKeyUtil.genFollowListKey(ENTITY_TYPE_USER, userId);
        Set<Integer> beFollowedIds = redisTemplate.opsForZSet().range(followListKey, offset, offset + limit - 1);
        if (beFollowedIds == null) {
            return null;
        }

        List<Map<String, Object>> follows = new ArrayList<>();
        for (int followUserId : beFollowedIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(followUserId));
            map.put("time", new Date(redisTemplate.opsForZSet().score(followListKey, followUserId).longValue()));
            map.put("isFollowing", isFollowing(loginUser == null ? null : loginUser.getId(), ENTITY_TYPE_USER, followUserId));
            follows.add(map);
        }
        return follows;
    }

    /**
     * 查询粉丝列表（支持分页）
     *
     * @param userId 查询哪个用户的粉丝列表
     * @param limit  每页显示几个
     * @param offset 从第几个开始显示
     * @return 粉丝列表（每一项包含两个信息：粉丝的user对象，关注时间）
     */
    @Override
    public List<Map<String, Object>> findFansList(int userId, int limit, int offset) {
        User loginUser = hostHolder.getUser();
        String fansListKey = RedisKeyUtil.genFansListKey(ENTITY_TYPE_USER, userId);
        Set<Integer> fansIds = redisTemplate.opsForZSet().range(fansListKey, offset, offset + limit - 1);

        if (fansIds == null) {
            return null;
        }

        List<Map<String, Object>> follows = new ArrayList<>();
        for (int fansUserId : fansIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(fansUserId));
            map.put("time", new Date(redisTemplate.opsForZSet().score(fansListKey, fansUserId).longValue()));
            map.put("isFollowing", isFollowing(loginUser == null ? null : loginUser.getId(), ENTITY_TYPE_USER, fansUserId));

            follows.add(map);
        }
        return follows;
    }

    /**
     * 查询某个用户的已关注（收藏）数量
     *
     * @param userId     用户id
     * @param entityType 实体类型（可以是用户，也可以是帖子等实体）
     * @return 已关注数量，或已收藏数量
     */
    @Override
    public long findFollowCount(int userId, int entityType) {
        String followListKey = RedisKeyUtil.genFollowListKey(entityType, userId);
        return redisTemplate.opsForZSet().zCard(followListKey);
    }

    /**
     * 查询粉丝数量或收藏数量
     *
     * @param entityType 实体类型（如果是用户，查询的就是粉丝数量，如果是帖子，查询的就是关注（收藏）这个帖子的人的数量）
     * @param entityId   实体id
     * @return 粉丝数量，或者收藏量
     */
    @Override
    public long findFansCount(int entityType, int entityId) {
        String fansListKey = RedisKeyUtil.genFansListKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(fansListKey);

    }

    /**
     * 判断是否已经关注
     *
     * @param userId     关注关系中的主体id
     * @param entityType 关注关系中的客体类型
     * @param entityId   关注关系中的客体id
     * @return 是否关注的boolean值
     */
    @Override
    public boolean isFollowing(Integer userId, Integer entityType, Integer entityId) {
        if (userId == null) {
            return false;
        }
        String followListKey = RedisKeyUtil.genFollowListKey(entityType, userId);
        Long rank = redisTemplate.opsForZSet().rank(followListKey, entityId);
        return !(rank == null);
    }
}
