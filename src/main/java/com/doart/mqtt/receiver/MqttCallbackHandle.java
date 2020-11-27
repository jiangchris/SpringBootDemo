package com.doart.mqtt.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MqttCallbackHandle {

    private static final Logger logger = LoggerFactory.getLogger(MqttCallbackHandle.class);

    public void handle(String topic, String payload) {

        logger.info("MqttCallbackHandle:" + topic + "---"+ payload);

        // 根据topic分别进行消息处理。
        if (topic.equalsIgnoreCase("testTopic")) {

            // 业务逻辑
        }
    }
}