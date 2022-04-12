package com.cgc.dao;

import com.cgc.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageMapperTest {
    @Autowired
    MessageMapper messageMapper;

    @Test
    void selectLatestMessages() {
        System.out.println(messageMapper.selectLatestMessages(111,5,0));
    }

    @Test
    void selectConversationCount() {
        System.out.println(messageMapper.selectConversationCount(111));
    }

    @Test
    void selectAllMessagesById() {
        System.out.println(messageMapper.selectAllMessagesById("111_112",5,0));
    }

    @Test
    void selectUnreadMessageCount() {
        System.out.println(messageMapper.selectUnreadMessageCount(111,"111_112"));
    }

    @Test
    void updateStatusOfMessage() {
    }

    @Test
    void insertMessage() {
        Message message = new Message();
        message.setConversationId("111_112");
        System.out.println(messageMapper.insertMessage(message));
    }

    @Test
    void selectNotices() {
        System.out.println(messageMapper.selectNotices(175,"like",5,0));
    }

    @Test
    void selectNoticeCount() {
        System.out.println(messageMapper.selectNoticeCount(175,"like"));
    }

    @Test
    void selectUnReadNoticeCount() {
        System.out.println(messageMapper.selectUnReadNoticeCount(175,"like"));
    }

    @Test
    void selectLatestNotice() {
        System.out.println(messageMapper.selectLatestNotice(175,"like"));
        System.out.println(messageMapper.selectLatestNotice(175,"comment"));
        System.out.println(messageMapper.selectLatestNotice(175,"follow"));

    }
}