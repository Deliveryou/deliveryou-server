package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.*;
import lombok.*;


/**
 * This class describes user (only shipper) package matching references.
 * Only user with role 'Shipper' is affected during matching session.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "matching_filter")
public class MatchingReferences {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "matching_radius")
    private double matchingRadius;

    @Column(name = "minimum_delivery_price")
    private double minimumDeliveryPrice;

    @Column(name = "maximum_delivery_distance")
    private double maximumDeliveryDistance;
}
