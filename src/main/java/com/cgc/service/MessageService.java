package com.cgc.service;

import com.cgc.entity.Message;

import java.util.List;

public interface MessageService {

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

}
