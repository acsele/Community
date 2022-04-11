package com.cgc.util;

public interface CommunityConstant {

    //定义激活转态
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;

    //默认的登录凭证超时时间
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    //点击记住我之后的凭证超时时间
    int REMEMBER_EXPIRED_SECONDS = 3600 * 12 * 100;

    //关注、评论、点赞相关
    int ENTITY_TYPE_COMMENT=2;
    int ENTITY_TYPE_POST=1;
    int ENTITY_TYPE_USER=3;  //只在关注相关业务中使用，数据只会存在redis中

    //kafka相关（主题名）
    String TOPIC_COMMENT="comment";
    String TOPIC_Like="like";
    String TOPIC_FOLLOW="follow";
    String TOPIC_PUBLISH="publish";

    //系统用户id
    int SYSTEM_ID=1;

}
