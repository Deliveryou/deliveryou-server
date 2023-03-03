package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @Column(name = "promotion_id")
    private UUID promotionId;

    @Column(name = "name")
    private String name;

    @Column(name = "discount_amount")
    private double discountAmount;
}
