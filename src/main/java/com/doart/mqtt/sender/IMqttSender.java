package com.doart.mqtt.sender;

import com.doart.mqtt.config.MqttConfig;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * MQTT生产者消息发送接口
 * MessagingGateway：指定默认的消息通道名称
 */
@Component
@MessagingGateway(defaultRequestChannel = MqttConfig.CHANNEL_NAME_OUT)
public interface IMqttSender {

    /**
     * 发送信息到MQTT服务器
     * @param data 发送的文本
     */
    void sendToMqtt(String data);

    /**
     * 发送信息到MQTT服务器
     * @param topic   主题
     * @param payload 消息主体
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic,
                    String payload);

    /**
     * 发送信息到MQTT服务器
     * <pre>
     * 0 至多一次，数据可能丢失
     * 1 至少一次，数据可能重复
     * 2 只有一次，且仅有一次，最耗性能
     * </pre>
     * @param topic   主题
     * @param qos     服务质量
     * @param payload 消息主体
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic,
                    @Header(MqttHeaders.QOS) int qos,
                    String payload);
}