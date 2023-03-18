package com.delix.deliveryou.spring.pojo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DeliveryPackage {
    private long id;
    private User user;
    private User shipper;
    private String photoUrl;
    private Promotion promotion;
    private double price;
    private Address senderAddress;
    private Address recipientAddress;
    private String recipientName;
    private String recipientPhone;
    private String note;
    private PackageType packageType;
    private OffsetDateTime creationDate;
}
