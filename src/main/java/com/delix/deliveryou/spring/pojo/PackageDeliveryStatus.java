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
    private static Map<Long, PackageDeliveryStatus> statusMapById = new HashMap<>();

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

//    @OneToMany(mappedBy = "status")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<DeliveryPackage> deliveryPackages = new HashSet<>();

    @PostLoad
    public void postLoad(){
        statusMapById.put(id,this);
        CANCELED = getById(1L);
        FINISHED = getById(2L);
        PENDING = getById(3L);
        DELIVERING = getById(4L);
        System.out.println("PackageDeliveryStatus Database called");
    }

    public static PackageDeliveryStatus getById(long id){
        return statusMapById.get(id);
    }


    @Transient
    public static  PackageDeliveryStatus CANCELED;
    @Transient
    public static  PackageDeliveryStatus FINISHED;
    @Transient
    public static  PackageDeliveryStatus PENDING;
    @Transient
    public static  PackageDeliveryStatus DELIVERING;
}
