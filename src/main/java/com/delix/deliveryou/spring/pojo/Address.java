package com.delix.deliveryou.spring.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;



@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

//    @Column(name = "street")
//    private String street;
//
//    @Column(name = "ward")
//    private String ward;
//
//    @Column(name = "district")
//    private String district;
//
//    @Column(name = "province")
//    private String province;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "country")
    private String country;

    @Column(name = "country_code")
    private String countryCode;

//    @JsonIgnore
//    @OneToMany(mappedBy = "senderAddress")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private final Set<DeliveryPackage> deliveryPackages1 = new HashSet<>();
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "recipientAddress")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private final Set<DeliveryPackage> deliveryPackages2 = new HashSet<>();

    public Address(double lat, double lon, String display_name, String country, String country_code) {
        this.longitude = String.valueOf(lon);
        this.latitude = String.valueOf(lat);
        this.displayName = display_name;
        this.country = country;
        this.countryCode = country_code;
    }
}
