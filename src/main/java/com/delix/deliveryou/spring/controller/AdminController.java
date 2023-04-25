package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.spring.component.DeliveryChargeAdvisor;
import com.delix.deliveryou.spring.model.SearchFilter;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.services.PackageService;
import com.delix.deliveryou.spring.services.UserService;
import com.delix.deliveryou.utility.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.delix.deliveryou.spring.model.SearchFilterType;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private PackageService packageService;
    @Autowired
    private DeliveryChargeAdvisor.Advisor advisor;

    @PostMapping("/all-regular-users")
    @CrossOrigin
    public ResponseEntity allRegularUsers(@RequestBody SearchFilter filter) {
        try {
            List<User> list = userService.getUsersWithFilter(UserRole.USER, filter);
            return new ResponseEntity(list, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/all-shippers")
    @CrossOrigin
    public ResponseEntity allShippers(@RequestBody SearchFilter filter) {
        try {
            List<User> list = userService.getUsersWithFilter(UserRole.SHIPPER, filter);
            return new ResponseEntity(list, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ban-user/{userId}")
    @CrossOrigin
    public ResponseEntity banUser(@PathVariable long userId) {
        try {
            boolean res = userService.markUserAsDeleted(userId, true);
            return new ResponseEntity((res) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unban-user/{userId}")
    @CrossOrigin
    public ResponseEntity unbanUser(@PathVariable long userId) {
        try {
            boolean res = userService.markUserAsDeleted(userId, false);
            return new ResponseEntity((res) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-delivery-packages/{userId}")
    @CrossOrigin
    public ResponseEntity allPackages(@PathVariable long userId) {
        try {
            var list = packageService.getAllPackages(userId);
            return new ResponseEntity(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/server-config/charge-advisor/get-config")
    @CrossOrigin
    public ResponseEntity getChargeAdvisorConfig() {
        try {
            var config = advisor.getAdvisorConfig();
            return new ResponseEntity(config, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
