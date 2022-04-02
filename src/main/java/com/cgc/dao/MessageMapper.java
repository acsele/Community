package com.cgc.dao;

import com.cgc.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表，每个会话只查最新的一条消息（用于私信列表显示）
    List<Message> selectLatestMessages(int userId, int limit, int offset);

    //查询当前会话数量
    int selectConversationCount(int userId);

    //查询某个会话的所有消息
    List<Message> selectAllMessagesById(String conversationId, int limit, int offset);

    //查询某个会话的所有消息数量（用于支持分页）
    int selectMessagesCount(String conversationId);

    //查询未读消息数量(当conversationId为空时查询所有会话的消息总数）
    int selectUnreadMessageCount(int userId, String conversationId);

    //修改消息状态（已读、未读）
    int updateStatusOfMessage(List<Integer> ids, int status);

    //新增消息
    int insertMessage(Message message);


}
