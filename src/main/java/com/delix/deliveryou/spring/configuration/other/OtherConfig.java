package com.delix.deliveryou.spring.configuration.other;

import com.delix.deliveryou.spring.component.DeliveryChargeAdvisor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
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
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public DeliveryChargeAdvisor.Advisor chargeAdvisorInstance() {
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

    @Bean
    public ObjectMapper JacksonObjectWriter() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public EntityManager entityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
