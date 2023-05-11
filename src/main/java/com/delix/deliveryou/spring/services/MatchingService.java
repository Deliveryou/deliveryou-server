package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.api.locationiq.LocationIQ;
import com.delix.deliveryou.spring.configuration.websocket.CommunicableUserContainer;
import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MatchingService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationIQ locationIQ;
    @Autowired
    public SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CommunicableUserContainer userContainer;

    private double distance(DeliveryPackage deliveryPackage) {
        // calculate distance
        var senderCoord = new LocationIQ.Coordinate(
                Double.parseDouble(deliveryPackage.getSenderAddress().getLatitude()),
                Double.parseDouble(deliveryPackage.getSenderAddress().getLongitude())
        );
        var repCoord = new LocationIQ.Coordinate(
                Double.parseDouble(deliveryPackage.getRecipientAddress().getLatitude()),
                Double.parseDouble(deliveryPackage.getRecipientAddress().getLongitude())
        );
        return locationIQ.distance(senderCoord, repCoord) / 1000;
    }

    /**
     * Notify matched drivers by filter via ws
     * @param deliveryPackage
     * @throws NullPointerException if [deliveryPackage] is null
     */
    public void matchPotentialDrivers(DeliveryPackage deliveryPackage) {
        /**
         * 1. package uploaded -> PackageService.matchingFilter
         * 2.
         *  a. { min_price, max_distance } = filter, { price, senderAddress, repAddress } = package
         *  b. distance = locationIQ(sender, rep)
         *  c. select * from driver where matching_filter.min <= price and max >= distance (driver with no active delivery)
         *  d. return values via ws
         *  e. drivers notified -> get GPS Location -> cal current loc to destination
         *  -> filter by radius before display
         */
        if (deliveryPackage == null)
            throw new NullPointerException("DeliveryPackage is null");

        try {
            // get drivers by filter (minPrice + maxDistance)
            var listOfDrivers = userRepository.getPotentialDrivers(deliveryPackage.getPrice(), distance(deliveryPackage));
            System.out.println(">>>> [Potential drivers]: " + listOfDrivers);
            var responseBody = objectMapper.writeValueAsString(deliveryPackage);

            for (var driver : listOfDrivers) {
                // online send if driver registered as being online
                if (userContainer.isOnline(driver.getId())) {
                    messagingTemplate.convertAndSendToUser(String.valueOf(driver.getId()), "/notification/package", responseBody);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
