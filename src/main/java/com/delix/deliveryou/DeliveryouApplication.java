package com.delix.deliveryou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
        //(exclude = { SecurityAutoConfiguration.class })
public class DeliveryouApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryouApplication.class, args);
    }


}
