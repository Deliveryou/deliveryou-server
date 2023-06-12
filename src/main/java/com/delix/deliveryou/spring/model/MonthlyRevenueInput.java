package com.delix.deliveryou.spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class MonthlyRevenueInput {
    @JsonProperty
    long userId;
    @JsonProperty
    List<String> months;
}
