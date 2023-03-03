package com.delix.deliveryou.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class WebSocketTest {
    @Autowired
    public SimpMessagingTemplate messagingTemplate;

    @GetMapping("/send")
    public ResponseEntity send() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        messagingTemplate.convertAndSendToUser("andie", "/notification", "data13142");
        return new ResponseEntity(HttpStatus.OK);
    }

    //@MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(String name) {
        return new Greeting("Hello, " + name);
    }
}
