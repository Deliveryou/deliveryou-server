package com.delix.deliveryou.spring.pojo;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private UserRole role;
    private String citizenId;
    private String profilePictureUrl;
    private MatchingReferences matchingReferences;
    private LocalDate dateOfBirth;
    private boolean deleted = false;

}