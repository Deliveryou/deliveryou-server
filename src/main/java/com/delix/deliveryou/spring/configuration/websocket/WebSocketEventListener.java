package com.delix.deliveryou.spring.configuration.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class WebSocketEventListener {

    // A method to handle STOMP session connected event
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {

        //You can access the headers, session id and user principal from the event object
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headerAccessor.getSessionId();
//        Principal user = headerAccessor.getUser();
//        // You can also perform some logic based on the headers or user information
//        // For example, you can close or invalidate the session if it does not contain correct data
//        if (user == null || !user.getName().equals("admin")) {
//            headerAccessor.getSessionAttributes().put("close", true);
//        }

        System.out.println("connected: " + headerAccessor.getHost());

    }

    // A method to handle STOMP session disconnected event
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        // You can access the headers, session id, close status and user principal from the event object
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headerAccessor.getSessionId();
//        CloseStatus closeStatus = event.getCloseStatus();
//        Principal user = headerAccessor.getUser();
//        // You can also perform some logic based on the headers or user information
//        // For example, you can remove the user from a chat room or notify other users about the disconnection
        System.out.println("disconnected");
    }
}