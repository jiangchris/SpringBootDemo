package com.doart.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;


@Component
@RabbitListener(queues = "TestTopic.man")
public class TopicManReceiver {

    @RabbitHandler
    public void process(Map testMessage) {

        System.out.println("TestTopicManReceiver消费者收到消息  : " + testMessage.toString());
    }
}
