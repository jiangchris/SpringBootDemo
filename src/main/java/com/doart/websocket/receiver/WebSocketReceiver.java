package com.doart.websocket.receiver;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/testWebSocket")
@Component
public class WebSocketReceiver {

    private static final Logger log = (Logger) LoggerFactory.getLogger(WebSocketReceiver.class);

    /**
     * 记录当前在线连接数.
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 存放所有在线的客户端.
     */
    private static Map<String, Session> clients = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {

        // 在线数加1
        onlineCount.incrementAndGet();
        clients.put(session.getId(), session);
        log.info("有新连接加入[{}]，当前在线人数[{}]", session.getId(), onlineCount.get());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {

        // 在线数减1
        onlineCount.decrementAndGet();
        clients.remove(session.getId());
        log.info("有一连接关闭[{}]，当前在线人数[{}]" , session.getId(), onlineCount.get());
    }

    @OnError
    public void onError(Session session, Throwable error) {

        log.error("发生错误:{}", error);
    }

    /**
     * 收到客户端消息后调用的方法.
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {

        log.info("服务端收到客户端[{}]的消息，内容[{}]", session.getId(), message);
        session.getBasicRemote().sendText("服务端推送："+ message);
    }
}