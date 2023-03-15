package com.delix.deliveryou.api.locationiq;

import lombok.Data;

import java.util.List;

@Data
public class Route {
    String geometry;
    String weight_name;
    double weight;
    double duration;
    double distance;
    List<Object> legs;
}
