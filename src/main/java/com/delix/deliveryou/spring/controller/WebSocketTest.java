package com.delix.deliveryou.spring.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketTest {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(Map<String, String> map) throws Exception {
        return new Greeting("Hello, " + map.get("name"));
    }
}
