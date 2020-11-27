package com.doart.rabbitmq.provider;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitmqProvider {

    /**
     * 使用RabbitTemplate,这提供了接收/发送等等方法
     */
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendMsg(String exchange, String queue, Object data) {

        rabbitTemplate.convertAndSend(exchange, queue, data);
    }
}
