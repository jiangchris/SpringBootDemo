package com.doart.mqtt.config;

import java.util.Map;

import com.doart.mqtt.receiver.MqttCallbackHandle;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * MQTT消费者配置.
 */
@Configuration
public class MqttConfig {

    /**
     * 入站通道名（消费者）.
     */
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
    /**
     * 出站通道名（生产者）.
     */
    public static final String CHANNEL_NAME_OUT = "mqttOutboundChannel";
    public static final String FACTORY_NAME = "mqttPahoClientFactory";
    public static final String OPTIONS_NAME = "mqttConnectOptions";

    /**
     * 日志.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

    /**
     * MQTT登录名.
     */
    @Value("${mqtt.username}")
    private String username;

    /**
     * MQTT登录密码.
     */
    @Value("${mqtt.password}")
    private String password;

    /**
     * MQTT连接地址.
     */
    @Value("#{'${mqtt.urls}'.split(',')}")
    private String[] urls;

    /**
     * 生产者clientId前缀（生产者）.
     */
    @Value("${mqtt.producer.clientIdPrefix}")
    private String producerClientIdPrefix;
    /**
     * 发送消息的默认topic（生产者）.
     */
    @Value("${mqtt.producer.topic}")
    private String producerDefaultTopic;

    /**
     * 消费者clientId前缀（消费者）.
     */
    @Value("${mqtt.consumer.clientIdPrefix}")
    private String consumerClientIdPrefix;
    /**
     * 消费者订阅主题（消费者）.
     */
    @Value("#{'${mqtt.consumer.topic}'.split(',')}")
    private String[] consumerTopic;
    /**
     * 消费者订阅主题设置服务质量（消费者）.
     */
    @Value("#{'${mqtt.consumer.qos}'.split(',')}")
    private int[] consumerQos;
    /**
     * 消费者操作完成的超时时长（消费者）.
     */
    @Value("${mqtt.consumer.completionTimeout}")
    private Integer completionTimeout;

    /**
     * MQTT会话心跳时间.
     */
    @Value("${mqtt.keepAlive}")
    private int keepAlive;

    @Autowired
    private MqttCallbackHandle mqttCallbackHandle;

    /**
     * MQTT连接器选项.
     * @return {@link org.eclipse.paho.client.mqttv3.MqttConnectOptions}
     */
    @Bean(name = OPTIONS_NAME)
    public MqttConnectOptions mqttConnectOptions() {

        MqttConnectOptions options = new MqttConnectOptions();
        // 设置连接的用户名
        options.setUserName(username);
        // 设置连接的密码
        options.setPassword(password.toCharArray());
        options.setServerURIs(urls);
        // 设置会话心跳时间，默认60秒。服务器会每隔（1.5 * KeepAlive）秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(keepAlive);
        // 设置是否清空session，false表示服务器会保留客户端的连接记录，true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 允许同时发送的消息数量，默认10
        options.setMaxInflight(10);
        // 设置超时时间，默认30秒
        options.setConnectionTimeout(30);
        // 自动重连
        options.setAutomaticReconnect(true);
        return options;
    }

    /**
     * MQTT客户端.
     * @param options {@link org.eclipse.paho.client.mqttv3.MqttConnectOptions}
     * @return {@link org.springframework.integration.mqtt.core.MqttPahoClientFactory}
     */
    @Bean(name = FACTORY_NAME)
    public MqttPahoClientFactory mqttPahoClientFactory(@Qualifier(OPTIONS_NAME) MqttConnectOptions options) {

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    /*--------------------------------消费者的配置--------------------------------*/
    /**
     * MQTT消费端订阅通道（消息入站）.
     * @return {@link org.springframework.messaging.MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_IN)
    public MessageChannel inboundChannel() {

        return new DirectChannel();
    }

    /**
     * MQTT消费端连接配置（消息入站）.
     * @param channel {@link org.springframework.messaging.MessageChannel}
     * @param factory {@link org.springframework.integration.mqtt.core.MqttPahoClientFactory}
     * @return {@link org.springframework.integration.core.MessageProducer}
     */
    @Bean
    public MessageProducer inbound(
            @Qualifier(CHANNEL_NAME_IN) MessageChannel channel,
            @Qualifier(FACTORY_NAME) MqttPahoClientFactory factory) {

        // 可以同时消费（订阅）多个Topic
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                consumerClientIdPrefix + "_in_bound", factory, consumerTopic);
        // 设置操作完成的超时时长，默认30000毫秒
        adapter.setCompletionTimeout(completionTimeout);
        // 配置默认消息转换器(qos=0, retain=false, charset=UTF-8)
        adapter.setConverter(new DefaultPahoMessageConverter());
        // 0 至多一次，数据可能丢失
        // 1 至少一次，数据可能重复
        // 2 只有一次，且仅有一次，最耗性能
        adapter.setQos(consumerQos);
        // 设置订阅通道
        adapter.setOutputChannel(channel);
        return adapter;
    }

    /**
     * MQTT消费端消息处理器（消息入站）.
     * @return {@link org.springframework.messaging.MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
    public MessageHandler inboundHandler() {

        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                Map<String, Object> headers = message.getHeaders();
//                for (Map.Entry<String, Object> entry : headers.entrySet()) {
//
//                    LOGGER.info("----MQTT Headers---- key = {}, value = {}",
//                            entry.getKey(), entry.getValue());
//                }
                Object topic = headers.get("mqtt_receivedTopic");
                //LOGGER.info("----MQTT消费---- top={}， getPayload={}", topic != null ? topic.toString() : "", message.getPayload());
                mqttCallbackHandle.handle(topic.toString(), message.getPayload().toString());
            }
        };
    }

    /*--------------------------------生产者的配置--------------------------------*/
    /**
     * MQTT生产端发布通道（消息出站）.
     * @return {@link org.springframework.messaging.MessageChannel}
     */
    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel outboundChannel() {

        return new DirectChannel();
    }

    /**
     * MQTT生产端发布处理器（消息出站）.
     * @param factory {@link org.springframework.integration.mqtt.core.MqttPahoClientFactory}
     * @return {@link org.springframework.messaging.MessageHandler}
     */
    @Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler outboundHandler(@Qualifier(FACTORY_NAME) MqttPahoClientFactory factory) {

        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                producerClientIdPrefix + "_out_bound", factory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultRetained(false);
        messageHandler.setDefaultTopic(producerDefaultTopic);
        return messageHandler;
    }
}