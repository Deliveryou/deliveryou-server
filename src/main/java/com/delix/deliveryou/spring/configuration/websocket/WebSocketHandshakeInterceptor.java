package com.delix.deliveryou.spring.configuration.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;


public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
//        Principal principal = request.getPrincipal();
//        wsHandler.afterConnectionEstablished();
        //System.out.println("------- principle: " + principal);
//        if (principal != null) {
//            String userId = principal.getName();
//            simpMessagingTemplate.convertAndSendToUser(userId, "/queue/greeting", "Hello " + userId);
//        }
    }
}
