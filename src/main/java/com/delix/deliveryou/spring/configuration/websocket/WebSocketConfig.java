package com.delix.deliveryou.spring.configuration.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/user");
        config.setApplicationDestinationPrefixes("/ws");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // with sockjs
        registry.addEndpoint("/websocket")
                //.setAllowedOrigins("*")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .withSockJS();
        // without sockjs
        //registry.addEndpoint("/ws-message").setAllowedOriginPatterns("*");
    }

    @Bean
    public CommunicableUserContainer communicableUserContainer() {
        return new CommunicableUserContainer().enableLogs();
    }

}
