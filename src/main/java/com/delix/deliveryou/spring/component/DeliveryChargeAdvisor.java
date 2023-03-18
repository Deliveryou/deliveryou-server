package com.delix.deliveryou.spring.component;

import com.delix.deliveryou.api.locationiq.LocationIQ;
import com.delix.deliveryou.spring.services.DeliveryService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DeliveryChargeAdvisor {
    public static class InvalidParamsException extends RuntimeException {
        public InvalidParamsException() {
            super();
        }

        public InvalidParamsException(String message) {
            super(message);
        }
    }
    @Value
    public static class RushHour {
        LocalTime start;
        LocalTime end;
        double additionalPrice;

        public RushHour(LocalTime startTime, LocalTime endTime, double additionalPrice) {
            if (startTime == null || endTime == null)
                throw new InvalidParamsException("[RushHourRange]: null params");

            if (additionalPrice < 0)
                throw new InvalidParamsException("[RushHourRange]: negative [additionalPrice]");

            if (startTime.isAfter(endTime))
                throw new InvalidParamsException("[RushHourRange]: [endTime] cannot be before [startTime]");

            this.start = startTime;
            this.end = endTime;
            this.additionalPrice = additionalPrice;
        }

        public boolean inRushHour(LocalTime time) {
            if (time == null)
                throw new InvalidParamsException("[RushHourRange]: [time] cannot be null");
            return (time.isAfter(start) && time.isBefore(end));
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Config {
        double firstKm = 1;
        double firstKmPrice;
        double pricePerEveryOtherKm;
        double platformFee;
        List<RushHour> rushHours = new ArrayList<>();


        public Config setFirstKm(int firstKm) {
            if (firstKm < 1)
                throw new InvalidParamsException("[Config]: [firstKm] must be at least 1 km");
            this.firstKm = firstKm;
            return this;
        }

        public Config setFirstKmPrice(double firstKmPrice) {
            if (firstKmPrice < 0)
                throw new InvalidParamsException("[Config]: negative [firstKmPrice]");
            this.firstKmPrice = firstKmPrice;
            return this;
        }

        public Config setPricePerEveryOtherKm(double pricePerEveryOtherKm) {
            if (pricePerEveryOtherKm < 0)
                throw new InvalidParamsException("[Config]: negative [pricePerEveryOtherKm]");
            this.pricePerEveryOtherKm = pricePerEveryOtherKm;
            return this;
        }

        public Config setRushHours(List<RushHour> rushHours) {
            if (rushHours != null && rushHours.size() > 0) {
                this.rushHours.addAll(rushHours);
            }
            return this;
        }

        public Config setPlatformFee(double platformFee) {
            if (platformFee < 0)
                throw new InvalidParamsException("[Config]: negative [platformFee]");
            this.platformFee = platformFee;
            return this;
        }
    }

    private Config config;
    private static Advisor advisor;
    @Autowired
    private DeliveryService packageService;
    @Autowired
    private LocationIQ locationIQ;

    public record AdvisorResponse(double price, double distance) {}

    public class Advisor {
        private Advisor() {}

        /**
         *
         * @param startingPoint
         * @param destination
         * @return distance between two points in meters
         */
        private double getDistance(LocationIQ.Coordinate startingPoint, LocationIQ.Coordinate destination) {
            return locationIQ.distance(
                    new LocationIQ.Coordinate(
                            startingPoint.lat(),
                            startingPoint.lon()
                            ),
                    new LocationIQ.Coordinate(
                            destination.lat(),
                            destination.lon()
                    ));
        }

        public AdvisorResponse getAdvisorPrice(LocationIQ.Coordinate startingPoint, LocationIQ.Coordinate destination, LocalTime creationTime) {
            final AdvisorResponse ERROR_VALUE = null;

            try {
                if (startingPoint == null || destination == null || creationTime == null)
                    return ERROR_VALUE;

                double distance = getDistance(startingPoint, destination);
                if (distance == -1)
                    return ERROR_VALUE;

                double originalDistance = distance;
                double calculatePrice = 0;

                // platform fee
                calculatePrice += config.getPlatformFee();

                // calculate [n] first km
                distance -= config.firstKm * 1000;
                calculatePrice += config.firstKmPrice;

                // calculate other km
                if (distance > 0) {
                    double otherKm = distance / 1000;
                    calculatePrice += config.pricePerEveryOtherKm * otherKm;
                } else
                    return new AdvisorResponse(calculatePrice, originalDistance);

                // additional fee during rush hour
                List<RushHour> rushHours = config.getRushHours();
                if (rushHours.size() == 0)
                    return new AdvisorResponse(calculatePrice, originalDistance);

                for (RushHour r : rushHours) {
                    if (r.inRushHour(creationTime)) {
                        calculatePrice += r.getAdditionalPrice();
                        break;
                    }
                }
                return new AdvisorResponse(calculatePrice, originalDistance);
            } catch (Exception e) {
                return ERROR_VALUE;
            }
        }

        public DeliveryChargeAdvisor getParent(){
            return DeliveryChargeAdvisor.this;
        }

    }

    public Advisor setConfig(Config config) {
        if (config == null)
            throw new InvalidParamsException("[DeliveryChargeAdvisor]: [config] cannot be null");
        this.config = config;

        if (advisor == null)
            advisor = new Advisor();
        return advisor;
    }

}
