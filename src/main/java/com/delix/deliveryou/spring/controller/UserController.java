package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.spring.configuration.websocket.CommunicableUserContainer;
import com.delix.deliveryou.utility.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private CommunicableUserContainer userContainer;

    @CrossOrigin
    @GetMapping("/i-am-online/{userId}")
    public ResponseEntity iAmOnline(@PathVariable Long userId) {
        var res = userContainer.registerAsActive(userId);
        return new ResponseEntity((res) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CrossOrigin
    @GetMapping("/i-am-offline/{userId}")
    public ResponseEntity iAmOffline(@PathVariable Long userId) {
        var res = userContainer.registerAsInactive(userId);
        return new ResponseEntity((res) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
