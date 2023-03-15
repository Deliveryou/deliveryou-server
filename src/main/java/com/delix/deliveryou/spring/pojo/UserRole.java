package com.delix.deliveryou.spring.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRole {
    public static final UserRole USER = new UserRole(1l, "USER");
    public static final UserRole SHIPPER = new UserRole(2l, "SHIPPER");
    public static final UserRole ADMIN = new UserRole(3l, "ADMIN");

    private long id;
    private String name;
}