package com.delix.deliveryou.spring.model;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class User {
    int id;
    String firstName;
    String lastName;
    String phone;
    String password;
    String role;
}