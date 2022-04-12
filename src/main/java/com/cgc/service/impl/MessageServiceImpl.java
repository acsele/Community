package com.cgc.service.impl;

import com.cgc.dao.MessageMapper;
import com.cgc.entity.Message;
import com.cgc.service.MessageService;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private HostHolder hostHolder;

    //查询最新消息列表
    @Override
    public List<Message> findLatestMessages(int userId, int limit, int offset) {
        return messageMapper.selectLatestMessages(userId, limit, offset);
    }

    //查询会话数量
    @Override
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    //查询某个会话中的所有消息
    @Override
    public List<Message> findMessages(String conversationId, int limit, int offset) {
        return messageMapper.selectAllMessagesById(conversationId, limit, offset);
    }

    //查询某个会话中所有消息数量
    @Override
    public int findMessagesCount(String conversationId) {
        return messageMapper.selectMessagesCount(conversationId);
    }

    //查询某个会话中的未读消息数量
    @Override
    public int findUnreadMessagesCount(int userId, String conversionId) {
        return messageMapper.selectUnreadMessageCount(userId, conversionId);
    }

    //添加消息
    @Override
    public int addMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    //读取消息
    @Override
    public int readMessage(List<Message> list) {
        List<Integer> ids = new ArrayList<>();
        if (list != null) {
            for (Message message : list) {
                //把发给我的、未读的消息加入列表
                if (hostHolder.getUser().getId() == (message.getToId()) && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        if (!ids.isEmpty()) {
            return messageMapper.updateStatusOfMessage(ids, 1);
        }
        return 0;
    }

    @Override
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    @Override
    public int findUnReadNoticeCount(int userId, String topic) {
        return messageMapper.selectUnReadNoticeCount(userId, topic);
    }

    @Override
    public List<Message> findNoticeList(int userId, String topic, int limit, int offset) {
        return messageMapper.selectNotices(userId, topic, limit, offset);
    }

    @Override
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }
}
