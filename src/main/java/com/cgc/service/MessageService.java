package com.cgc.service;

import com.cgc.entity.Message;

import java.util.List;

public interface MessageService {

    //私信相关

    //查询最新消息列表（用于显示在私信列表页面）
    List<Message> findLatestMessages(int userId, int limit, int offset);

    //查询当前用户会话数量
    int findConversationCount(int userId);

    //查询某个会话的消息列表
    List<Message> findMessages(String conversationId, int limit, int offset);

    //查询某个会话的消息数量(用于计算分页相关信息）
    int findMessagesCount(String conversationId);

    //查询未读消息数量
    int findUnreadMessagesCount(int userId, String conversionId);

    //新增消息
    int addMessage(Message message);

    //读取消息
    int readMessage(List<Message> ids);


    //系统通知相关

    //查询某个主题的全部通知数量
    int findNoticeCount(int userId, String topic);

    //查询未读通知数量
    int findUnReadNoticeCount(int userId, String topic);

    //查询通知列表
    List<Message> findNoticeList(int userId, String topic, int limit, int offset);

    //查询某个主题下的最新通知
    Message findLatestNotice(int userId, String topic);

}
