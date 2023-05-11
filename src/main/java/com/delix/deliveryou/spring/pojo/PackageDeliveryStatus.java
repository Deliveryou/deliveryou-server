package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "package_status")
public class PackageDeliveryStatus {
    @Id
    @Column(name = "order_state_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

//    @OneToMany(mappedBy = "status")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<DeliveryPackage> deliveryPackages = new HashSet<>();

    @Transient
    public static final PackageDeliveryStatus CANCELED = new PackageDeliveryStatus(1, "CANCELED");
    @Transient
    public static final PackageDeliveryStatus FINISHED = new PackageDeliveryStatus(2, "FINISHED");
    @Transient
    public static final PackageDeliveryStatus PENDING = new PackageDeliveryStatus(3, "PENDING");
    @Transient
    public static final PackageDeliveryStatus DELIVERING = new PackageDeliveryStatus(4, "DELIVERING");
}
