package com.doart.controller;

import javax.annotation.Resource;
import com.alibaba.fastjson.JSONObject;
import com.doart.mqtt.sender.IMqttSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MQTT消息发送
 *
 * @author BBF
 */
@Controller
@RequestMapping(value = "/")
public class MqttController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttController.class);

    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender iMqttSender;

    /**
     * 发送MQTT消息.
     * @return 返回
     */
    @ResponseBody
    @PostMapping(value = "/mqtt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendMqtt2() {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("transId", System.currentTimeMillis()/1000);
        iMqttSender.sendToMqtt("TrackRobot_Query_Parameters/10009",0, jsonObj.toJSONString());
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}