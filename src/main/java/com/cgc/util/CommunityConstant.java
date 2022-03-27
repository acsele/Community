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
}
