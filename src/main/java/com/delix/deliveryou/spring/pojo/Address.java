package com.delix.deliveryou.spring.pojo;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private long id;
    private double latitude;
    private double longitude;
//    private String street;
//    private String ward;
//    private String district;
//    private String province;
    private String displayName;
    private String country;
    private String countryCode;

}
