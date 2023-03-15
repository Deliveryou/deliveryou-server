package com.delix.deliveryou.graphql.controller;

import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.pojo.Promotion;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.services.PromotionService;
import com.delix.deliveryou.spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserGraphqlController {
    @Autowired
    private UserService userService;
    @Autowired
    private PromotionService promotionService;

    @QueryMapping
    public User userById(@Argument Integer id) {
        User user = ((JWTUserDetails) userService.loadUserById(Long.valueOf(id))).getUserObject();
        System.out.println("- User: " + user.toString());
        return user;
    }

    @SchemaMapping
    public UserRole userRole(User user) {
        return user.getRole();
    }

    @QueryMapping
    public List<Promotion> applicablePromotion(@Argument Integer userId) {
        return promotionService.getApplicablePromotion(Long.valueOf(userId));
    }
}
