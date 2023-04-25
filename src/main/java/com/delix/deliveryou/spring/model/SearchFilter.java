package com.delix.deliveryou.spring.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class SearchFilter {
    /**
     * Get value from SearchFilterType
     */
    private int type;
    private String value;
    private int startIndex;
    private int endIndex;

}
