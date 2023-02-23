package com.delix.deliveryou.spring.model;

import lombok.Builder;
import lombok.Value;

@Value
public class User {
    int id;
    String firstName;
    String lastName;
    String phone;
    String password;
}