package com.cgc.other;

import com.cgc.CommunityApplication;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

@SpringBootTest
public class KafkaTest {
    @Resource
    KafkaProducer kafkaProducer;


    //测试
    @Test
    public void test() throws InterruptedException {
        kafkaProducer.sendMessage("quickstart-events", "hello");
        kafkaProducer.sendMessage("quickstart-events", "hello");
        kafkaProducer.sendMessage("quickstart-events", "hello");
        kafkaProducer.sendMessage("quickstart-events", "hello");
        Thread.sleep(10*1000);
    }

}

//生产者
@Component
class KafkaProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}

//消费者
@Component
class KafkaConsumer {
    @KafkaListener(topics = {"quickstart-events"})
    //访问该方法时会自动将接收到的主题中的信息封装到ConsumerRecord对象中
    public void consumer(ConsumerRecord in) {
        System.out.println(in.value());

    }
}