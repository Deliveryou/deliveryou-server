package com.delix.deliveryou.spring.pojo;

import lombok.*;


/**
 * This class describes user (only shipper) package matching references.
 * Only user with role 'Shipper' is affected during matching session.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class MatchingReferences {
    long id;
    double matchingRadius;
    double minimumDeliveryPrice;
    double maximumDeliveryDistance;
}
