package com.delix.deliveryou.spring.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @Column(name = "dob")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "profile_image")
    private String profilePictureUrl;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "citizen_id")
    private String citizenId;

    @Column(name = "average_rating")
    private Float averageRating;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "filter")
    private MatchingReferences matchingReferences;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_role")
    private UserRole role;

//    @JsonIgnore
//    @OneToMany(mappedBy = "user")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private final Set<DeliveryPackage> deliveryPackages1 = new HashSet<>();
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "shipper")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private final Set<DeliveryPackage> deliveryPackages2 = new HashSet<>();


}