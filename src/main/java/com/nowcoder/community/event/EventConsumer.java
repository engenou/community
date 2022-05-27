package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.Constant;
import com.sun.javaws.jnl.RContentDesc;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.omg.CORBA.ObjectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  消费消息
 *
 * @author Eugen
 * @creat 2022-05-11 10:19
 */
@Slf4j
@Component
public class EventConsumer {

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {Constant.TOPIC_COMMENT,Constant.TOPIC_FOLLOW,Constant.TOPIC_LIKE})
    public void handleCommnentMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.error("消息内容为空");
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.error("消息格式错误");
        }

        // 发送站内消息
        Message message = new Message();
        message.setFromId(Constant.SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        // 消息内容：拼装的json字符串
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if(!event.getData().isEmpty()){
            for(Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);

    }

}
