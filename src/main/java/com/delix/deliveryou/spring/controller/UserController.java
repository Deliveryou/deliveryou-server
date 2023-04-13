package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.exception.LogicViolationException;
import com.delix.deliveryou.spring.configuration.websocket.CommunicableUserContainer;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.services.UserService;
import com.delix.deliveryou.utility.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.Map;

@Controller
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private CommunicableUserContainer userContainer;
    @Autowired
    private UserService userService;

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

    @CrossOrigin
    @GetMapping("/can-use-phone/{phoneNumber}")
    public ResponseEntity canUsePhone(@PathVariable String phoneNumber) {
        try {
            boolean result = userService.phoneExists(phoneNumber);
            return new ResponseEntity(JsonResponseBody.build(
                    "exists", result
            ), HttpStatus.OK);
        } catch (LogicViolationException ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/verify-password")
    public ResponseEntity verifyPassword(@RequestBody Map<String, String> map) {
        try {
            long userId = Long.parseLong(map.get("userId"));
            String password = map.get("password");

            boolean result = userService.verifyPassword(userId, password);
            return new ResponseEntity(JsonResponseBody.build(
                    "matched", result
            ), HttpStatus.OK);

        } catch (NullPointerException | ClassCastException | NumberFormatException | HttpBadRequestException ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/update-user")
    public ResponseEntity updateUser(@RequestBody User updatedUser) {
        try {
            System.out.println("updated user: " + updatedUser);
            var result = userService.updateUser(updatedUser);
            return new ResponseEntity(JsonResponseBody.build(
                    "updated", result
            ), HttpStatus.OK);

        } catch (HttpBadRequestException ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
