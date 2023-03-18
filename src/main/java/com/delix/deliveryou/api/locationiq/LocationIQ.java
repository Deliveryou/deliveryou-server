package com.delix.deliveryou.api.locationiq;

import com.delix.deliveryou.exception.InternalServerHttpException;
import com.delix.deliveryou.exception.InternalServerLogicProcessingException;
import com.delix.deliveryou.spring.pojo.Address;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.IllegalFormatException;

@Component
public class LocationIQ {
    public static class InvalidGeoParams extends RuntimeException {
        public InvalidGeoParams() {
            super();
        }
        public InvalidGeoParams(String message) {
            super(message);
        }
    }
    public record Coordinate(double lat, double lon){}
    @Value("${locationiq.key}")
    private String API_KEY;

    /**
     *
     * @param startingPoint
     * @param destination
     * @return distance in meters between 2 points
     * @throws InvalidGeoParams if either param is null
     */
    public double distance(Coordinate startingPoint, Coordinate destination) {
        if (startingPoint == null || destination == null)
            throw new InvalidGeoParams();

        RestTemplate restTemplate = new RestTemplate();
        String endpoint = String.format("https://us1.locationiq.com/v1/directions/driving/%s,%s" +
                ";%s,%s?key=%s&steps=true&alternatives=true&geometries=polyline&overview=full",
                startingPoint.lon, startingPoint.lat, destination.lon, destination.lat, API_KEY);
        try {
            Direction direction = restTemplate.getForObject(endpoint, Direction.class);
            return direction.getRoutes().get(0).distance;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     *
     * @param coordinate
     * @return an Address object constructed from the coordinate
     * @throws InvalidGeoParams if coordinate is null
     * @throws InternalServerLogicProcessingException if internal logic processing encounters exceptions
     * @throws InternalServerHttpException if internal api call processing encounter exceptions
     */
    public Address reverseGeo(Coordinate coordinate) {
        if (coordinate == null)
            throw new InvalidGeoParams();

        RestTemplate restTemplate = new RestTemplate();
        try {
            String endpoint = String.format("https://us1.locationiq.com/v1/reverse?key=%s&lat=%s&lon=%s&format=json&normalizeaddress=1",
                    API_KEY, coordinate.lat(), coordinate.lon());

            LocationIQData data = restTemplate.getForObject(endpoint, LocationIQData.class);
            Address address = new Address(0, data.lat, data.lon, data.display_name,
                    data.address.country, data.address.country_code);
            return address;
        } catch (IllegalFormatException ex) {
            throw new InternalServerLogicProcessingException("Invalid endpoint string format");
        } catch (Exception ex) {
            throw new InternalServerHttpException();
        }
    }
}
