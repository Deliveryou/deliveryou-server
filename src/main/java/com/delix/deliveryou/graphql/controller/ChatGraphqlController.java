package com.delix.deliveryou.graphql.controller;

import com.delix.deliveryou.spring.pojo.ChatSession;
import com.delix.deliveryou.spring.repository.ChatRepository;
import com.delix.deliveryou.spring.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatGraphqlController {
    @Autowired
    private ChatService chatService;

//    @QueryMapping
//    public List<ChatSession> allChatSessions(@Argument Integer userId) {
//        return chatService.getChatSessions(Long.valueOf(userId), null);
//    }

    @QueryMapping
    public ChatSession chatSession(@Argument Integer userId, @Argument Integer shipperId) {
        try {
            System.out.println("graph ql get chat");
            return chatService.getChatSession(userId, shipperId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
