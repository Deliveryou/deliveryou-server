package com.delix.deliveryou.spring.configuration.other;

import com.delix.deliveryou.spring.component.DeliveryChargeAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class OtherConfig {
    @Autowired
    private DeliveryChargeAdvisor chargeAdvisor;

    @Bean
    public DeliveryChargeAdvisor.Advisor deliveryChargeAdvisor() {
        List<DeliveryChargeAdvisor.RushHour> rushHourList = Arrays.asList(
                new DeliveryChargeAdvisor.RushHour(
                        LocalTime.of(6, 30, 0),
                        LocalTime.of(8, 0, 0),
                        5000
                ),
                new DeliveryChargeAdvisor.RushHour(
                        LocalTime.of(11, 0, 0),
                        LocalTime.of(13, 0, 0),
                        7000
                ),
                new DeliveryChargeAdvisor.RushHour(
                        LocalTime.of(16, 0, 0),
                        LocalTime.of(18, 0, 0),
                        6000
                )
        );

        DeliveryChargeAdvisor.Config config = new DeliveryChargeAdvisor.Config();
        config.setFirstKm(2)
                .setFirstKmPrice(12500)
                .setPricePerEveryOtherKm(4300)
                .setPlatformFee(2000)
                .setRushHours(rushHourList);

        return chargeAdvisor.setConfig(config);
    }
}
