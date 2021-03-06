package com.cgc.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";

    //点赞相关 key 前缀
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    //关注相关 key 前缀
    private static final String PREFIX_FOLLOW = "follow";
    private static final String PREFIX_FANS = "fans";

    /**
     * 生成对实体赞的key，这个key里面存的都是为当前实体点过赞的用户id。（实体可能是帖子、回复、评论）
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 向redis中存储实体的赞时使用的key
     */
    public static String genEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 生成对用户赞的key（这个key中存储的都是为该用户点过赞的用户id，可以用于显示在个人主页：该用户获赞数量，赞我的人列表）
     *
     * @param userId 用户id
     * @return 向redis中存储对用户的赞时使用的key
     */
    public static String genUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 生成已关注列表的key
     * 格式：follow:userId:entityType
     *
     * @param userId     哪个用户的关注（收藏）列表
     * @param entityType 可以是关注列表或收藏列表
     * @return 生成的关注或收藏列表key
     */
    public static String genFollowListKey(int entityType, int userId) {
        return PREFIX_FOLLOW + SPLIT + entityType + SPLIT + userId;
    }

    /**
     * 生成用户的粉丝列表，或是帖子的关注着列表的key
     * key格式：fans:entityType:entityId
     *
     * @param entityType 实体类型，可以是用户，帖子等
     * @param entityId   实体id
     * @return 生成的粉丝或关注着列表
     */
    public static String genFansListKey(int entityType, int entityId) {
        return PREFIX_FANS + SPLIT + entityType + SPLIT + entityId;
    }

}
