package com.delix.deliveryou.spring.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_role_name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    public UserRole(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Transient
    public static final UserRole USER = new UserRole(1, "USER");
    @Transient
    public static final UserRole SHIPPER = new UserRole(2, "SHIPPER");
    @Transient
    public static final UserRole ADMIN = new UserRole(3, "ADMIN");

}