package com.cgc.event;

import com.alibaba.fastjson.JSONObject;
import com.cgc.dao.MessageMapper;
import com.cgc.entity.Event;
import com.cgc.entity.Message;
import com.cgc.service.MessageService;
import com.cgc.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;


    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_Like})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            System.out.println("消息内容为空");
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            System.out.println("消息格式错误");
            return;
        }

        //将数据写入数据库的Message表
        Message message = new Message();
        message.setFromId(SYSTEM_ID);
        message.setToId(event.getUserId());
        //这里存储的不是真的conversationId，而是一个主题名，但从用户的角度看，也是一个会话
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> messageText = new HashMap<>();
        messageText.put("userId", event.getUserId());
        messageText.put("entityType", event.getEntityType());
        messageText.put("entityId", event.getEntityId());
        //event中的data字段中存储的其他数据
        if (event.getData() != null) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                messageText.put(entry.getKey(),entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(messageText));

        messageMapper.insertMessage(message);
//        System.out.println("66666666666666666666666"+messageService.addMessage(message));
    }
}
