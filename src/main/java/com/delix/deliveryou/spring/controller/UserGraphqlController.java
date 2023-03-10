package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.model.User;
import com.delix.deliveryou.spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserGraphqlController {
    @Autowired
    private UserService userService;

    @QueryMapping
    public User userById(@Argument Integer id) {
        User user = ((JWTUserDetails) userService.loadUserById(Long.valueOf(id))).getUserObject();
        System.out.println("- User: " + user.toString());
        return user;
    }
}
