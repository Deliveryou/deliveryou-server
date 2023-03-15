package com.delix.deliveryou.api.locationiq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Component
public class LocationIQ {
    public record Coordinate(double lat, double lon){}
    @Value("${locationiq.key}")
    private String API_KEY;

    public double distance(Coordinate startingPoint, Coordinate destination) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = String.format("https://us1.locationiq.com/v1/directions/driving/%s,%s" +
                ";%s,%s?key=%s&steps=true&alternatives=true&geometries=polyline&overview=full",
                startingPoint.lat, startingPoint.lon, destination.lat, destination.lon, API_KEY);
        Direction direction = restTemplate.getForObject(endpoint, Direction.class);
        try {
            return direction.getRoutes().get(0).distance;
        } catch (Exception e) {
            return -1;
        }
    }
}
