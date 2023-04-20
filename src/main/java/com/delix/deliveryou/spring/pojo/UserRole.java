package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "user_role")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    private static Map<Integer, UserRole> rolesById = new HashMap<>();

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_role_name")
    private String name;

    @OneToMany(mappedBy = "role")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @PostLoad
    public void postLoad() {
        rolesById.put(id, this);
        USER = getById(1);
        SHIPPER = getById(2);
        ADMIN = getById(3);
        System.out.println("Usertype database called!");
    }

    public static UserRole getById(int id) {
        return rolesById.get(id);
    }


    @Transient
    public static UserRole USER;
    @Transient
    public static UserRole SHIPPER;
    @Transient
    public static UserRole ADMIN;

    public UserRole getADMIN() {
        return ADMIN;
    }

    public UserRole getUSER() {
        return USER;
    }

    public UserRole getSHIPPER() {
        return SHIPPER;
    }
}