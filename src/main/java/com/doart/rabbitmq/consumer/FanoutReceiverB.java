package com.doart.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "TestFanout.B")
public class FanoutReceiverB {

    @RabbitHandler
    public void process(Map testMessage) {

        System.out.println("FanoutReceiverB消费者收到消息  : " +testMessage.toString());
    }
}