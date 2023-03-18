package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.api.locationiq.LocationIQ;
import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.exception.InternalServerHttpException;
import com.delix.deliveryou.spring.component.DeliveryChargeAdvisor;
import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.pojo.*;
import com.delix.deliveryou.spring.services.DeliveryService;
import com.delix.deliveryou.spring.services.PromotionService;
import com.delix.deliveryou.spring.services.UserService;
import com.delix.deliveryou.utility.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeliveryPackageController {
    @Autowired
    private DeliveryChargeAdvisor.Advisor chargeAdvisor;
    @Autowired
    private UserService userService;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private LocationIQ locationIQ;
    @Autowired
    private DeliveryService packageService;

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

    @CrossOrigin
    @PostMapping("/user/package/upload")
    public ResponseEntity createPackage(@RequestBody DeliveryPackage deliveryPackage) {
        try {
            User sender = ((JWTUserDetails) userService.loadUserById(deliveryPackage.getUser().getId())).getUserObject();
            Promotion promotion = promotionService.loadPromotion(deliveryPackage.getPromotion().getId());
            Address senderAddress = locationIQ.reverseGeo(new LocationIQ.Coordinate(
                        deliveryPackage.getSenderAddress().getLatitude(),
                        deliveryPackage.getSenderAddress().getLongitude()
                    ));
            Address recipientAddress = locationIQ.reverseGeo(new LocationIQ.Coordinate(
                    deliveryPackage.getRecipientAddress().getLatitude(),
                    deliveryPackage.getRecipientAddress().getLongitude()
            ));
            PackageType packageType = PackageType.getTypeByName(deliveryPackage.getPackageType().getName());

            if (promotion == null || senderAddress == null || recipientAddress == null || packageType == null)
                throw new HttpBadRequestException();

            OffsetDateTime createTime = OffsetDateTime.now();

            deliveryPackage.setUser(sender);
            deliveryPackage.setPromotion(promotion);
            deliveryPackage.setSenderAddress(senderAddress);
            deliveryPackage.setRecipientAddress(recipientAddress);
            deliveryPackage.setPackageType(packageType);
            deliveryPackage.setCreationDate(createTime);

            System.out.println("------ package: " + deliveryPackage);
            packageService.savePackage(deliveryPackage);

            return new ResponseEntity(HttpStatus.OK);

        } catch (UsernameNotFoundException | LocationIQ.InvalidGeoParams | HttpBadRequestException  ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
