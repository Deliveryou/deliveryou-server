package com.delix.deliveryou.spring.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeliveryPackage {
    private long id;
    private User user;
    private User shipper;
    private String photoUrl;
    private Promotion promotion;
    private double price;
    private Address senderAddress;
    private Address recipientAddress;
    private Address recipientName;
    private Address recipientPhone;
    private String note;
    private PackageType packageType;

    private OffsetDateTime creationDate;
}
