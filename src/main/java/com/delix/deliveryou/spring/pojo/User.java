package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "dob")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "profile_image")
    private String profilePictureUrl;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "citizen_id")
    private String citizenId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "filter")
    private MatchingReferences matchingReferences;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_role")
    private UserRole role;

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Set<DeliveryPackage> deliveryPackages1 = new HashSet<>();

    @OneToMany(mappedBy = "shipper")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Set<DeliveryPackage> deliveryPackages2 = new HashSet<>();


}