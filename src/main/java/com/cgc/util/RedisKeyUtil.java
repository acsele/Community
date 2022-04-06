package com.cgc.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";

    //点赞 key 前缀
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    //关注 key 前缀
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

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
     * 生成对用户赞的key（这个key中存储的都是为该用户点过赞的用户id）
     *
     * @param userId 用户id
     * @return 向redis中存储对用户的赞时使用的key
     */
    public static String genUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }


}
