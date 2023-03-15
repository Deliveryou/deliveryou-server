package com.delix.deliveryou.api.locationiq;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class Direction {
    String code;
    List<Route> routes;
    List<Object> waypoints;
}
