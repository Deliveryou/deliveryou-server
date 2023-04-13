package com.delix.deliveryou.spring.pojo;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class PackageDeliveryStatus {
    public static final PackageDeliveryStatus CANCELED = new PackageDeliveryStatus(1l, "CANCELED");
    public static final PackageDeliveryStatus FINISHED = new PackageDeliveryStatus(2l, "FINISHED");
    public static final PackageDeliveryStatus PENDING = new PackageDeliveryStatus(3l, "PENDING");
    public static final PackageDeliveryStatus DELIVERING = new PackageDeliveryStatus(4l, "DELIVERING");


    private long id;
    private String name;
}
