package com.cgc.service;

import java.util.List;
import java.util.Map;

public interface FollowService {

    /**
     * 关注某个用户：当用户点击关注（或取消关注）按钮时执行该方法
     *
     * @param followUserId         点关注的人的userId
     * @param beFollowedEntityType 被关注的实体类型
     * @param beFollowedEntityId   被关注的实体的id
     */
    void follow(int followUserId, int beFollowedEntityType, int beFollowedEntityId);

    /**
     * 查询关注列表（支持分页）
     *
     * @param userId 要查询哪个用户的关注列表
     * @param limit  每页显示几个
     * @param offset 从第几个开始
     * @return 用于展示到前端关注列表中的信息集合（每一项主要包括两部分：关注着的user对象，关注的时间）
     */
    List<Map<String, Object>> findFollowList(int userId, int limit, int offset);

    /**
     * 查询粉丝列表（支持分页）
     *
     * @param userId 查询哪个用户的粉丝列表
     * @param limit  每页显示几个
     * @param offset 从第几个开始显示
     * @return 粉丝列表（每一项包含两个信息：粉丝的user对象，关注时间）
     */
    List<Map<String, Object>> findFansList(int userId, int limit, int offset);

    /**
     * 查询某个用户的已关注（收藏）数量
     *
     * @param userId     用户id
     * @param entityType 实体类型（可以是用户，也可以是帖子等实体）
     * @return 已关注数量，或已收藏数量
     */
    long findFollowCount(int userId, int entityType);

    /**
     * 查询粉丝数量或收藏数量
     *
     * @param entityType 实体类型（如果是用户，查询的就是粉丝数量，如果是帖子，查询的就是关注（收藏）这个帖子的人的数量）
     * @param entityId   实体id
     * @return 粉丝数量，或者收藏量
     */
    long findFansCount(int entityType, int entityId);

    /**
     * 判断是否已经关注
     *
     * @param userId     关注关系中的主体id
     * @param entityType 关注关系中的客体类型
     * @param entityId   关注关系中的客体id
     * @return 是否关注的boolean值
     */
    boolean isFollowing(Integer userId, Integer entityType, Integer entityId);


}
