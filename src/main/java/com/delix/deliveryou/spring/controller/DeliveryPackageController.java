package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.api.locationiq.LocationIQ;
import com.delix.deliveryou.exception.InternalServerHttpException;
import com.delix.deliveryou.spring.component.DeliveryChargeAdvisor;
import com.delix.deliveryou.utility.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeliveryPackageController {
    @Autowired
    private DeliveryChargeAdvisor.Advisor chargeAdvisor;

    @CrossOrigin
    @PostMapping("/user/package/advisor-price")
    public ResponseEntity getAdvisorPrice(@RequestBody Map<String, String> map) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            double startingPoint_lat = Double.parseDouble(map.get("starting_point_lat"));
            double startingPoint_lon = Double.parseDouble(map.get("starting_point_lon"));
            double destination_lat = Double.parseDouble(map.get("destination_lat"));
            double destination_lon = Double.parseDouble(map.get("destination_lon"));
            String creationTimeRaw = map.get("creation_time");
            // hh:mm:ss(:ns)
            String[] partials = creationTimeRaw.split(":");
            LocalTime creationTime = LocalTime.of(
                    Integer.parseInt(partials[0]),
                    Integer.parseInt(partials[1]),
                    Integer.parseInt(partials[2])
                    );

            DeliveryChargeAdvisor.AdvisorResponse response = chargeAdvisor.getAdvisorPrice(
                    new LocationIQ.Coordinate(startingPoint_lat, startingPoint_lon),
                    new LocationIQ.Coordinate(destination_lat, destination_lon),
                    creationTime
                    );

            if (response == null)
                throw new InternalServerHttpException();

            return new ResponseEntity(JsonResponseBody.build(
                    "price", response.price(),
                    "distance", response.distance()
            ), httpStatus);

        } catch (NumberFormatException | DateTimeException | NullPointerException exception) {
            httpStatus = HttpStatus.BAD_REQUEST;
            exception.printStackTrace();
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        return new ResponseEntity(httpStatus);
    }
}
