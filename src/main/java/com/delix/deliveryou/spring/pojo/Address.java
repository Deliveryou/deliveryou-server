package com.delix.deliveryou.spring.pojo;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Data
@Setter
public class Address {
    private long id;
    private double longitude;
    private double latitude;
    private String street;
    private String ward;
    private String district;
    private String province;
    private String country;
    private String countryCode;

}
