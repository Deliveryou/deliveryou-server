package com.delix.deliveryou.spring.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class MonthlyRevenueResult {
    String month;
    double value;
}
