package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Eugen
 * @creat 2022-05-10 19:59
 */
@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void test(){
        kafkaProducer.sendMessage("test", "你好");
        kafkaProducer.sendMessage("test", "在吗");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// 生产者主动是调的
@Component
class KafkaProducer{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic, content);
    }
}

@Component
class KafkaConsumer{

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}
