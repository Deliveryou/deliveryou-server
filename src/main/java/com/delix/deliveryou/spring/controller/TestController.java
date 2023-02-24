package com.delix.deliveryou.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TestController {
    @GetMapping("/test1")
    public ResponseEntity t1() {
        return new ResponseEntity("1", HttpStatus.OK);
    }

    // authenticated
    @PostMapping("/test2")
    public ResponseEntity test() {
        return new ResponseEntity(HttpStatus.OK);
    }

    // role USER
    @PostMapping("/test3")
    public ResponseEntity test3() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
