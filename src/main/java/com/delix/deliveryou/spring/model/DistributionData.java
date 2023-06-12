package com.delix.deliveryou.spring.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class DistributionData {
    String type;
    long count;
}
