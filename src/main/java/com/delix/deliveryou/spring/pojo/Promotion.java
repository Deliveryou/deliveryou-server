package com.delix.deliveryou.spring.pojo;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    private long id;
    private String promoCode;
    private String description;

    // eg: 0.15 = 15%
    private float discountPercentage;
    private double maximumDiscountAmount;
    // delivery package must be at least [applicablePrice] to apply promotion
    private double applicablePrice;
    private OffsetDateTime expireDate;
}
