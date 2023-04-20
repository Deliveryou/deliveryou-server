package com.delix.deliveryou.spring.pojo;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @Column(name = "promotion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String promoCode;

    @Column(name = "description")
    private String description;

    // eg: 0.15 = 15%
    @Column(name = "discount_percentage")
    private float discountPercentage;

    @Column(name = "maximum_discount_amount")
    private double maximumDiscountAmount;

    // delivery package must be at least [applicablePrice] to apply promotion
    @Column(name = "applicable_price")
    private double applicablePrice;

    @Column(name = "expire_date")
    private OffsetDateTime expireDate;

    @OneToMany(mappedBy = "promotion")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Set<DeliveryPackage> deliveryPackages = new HashSet<>();
}
