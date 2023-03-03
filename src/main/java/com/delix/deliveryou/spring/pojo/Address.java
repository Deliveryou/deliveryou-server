package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
public class Address {
    @Id
    @Column(name = "address_id")
    private UUID addressId;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "street")
    private String street;

    @Column(name = "ward")
    private String ward;

    @Column(name = "district")
    private String district;

    @Column(name = "province")
    private String province;

    @Column(name = "country")
    private String country;

    @Column(name = "country_code")
    private String countryCode;

}
