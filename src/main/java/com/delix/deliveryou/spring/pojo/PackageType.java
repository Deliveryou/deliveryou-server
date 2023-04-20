package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "package_type")
public class PackageType {

    private static Map<Long, PackageType> packageTypeMapById = new HashMap<>();
    private static Map<String, PackageType> packageTypeMapByName = new HashMap<>();

    @Transient
    public static PackageType FOOD;
    @Transient
    public static PackageType CLOTHING;
    @Transient
    public static PackageType ELECTRONICS;
    @Transient
    public static PackageType FRAGILE;
    @Transient
    public static PackageType OTHER;


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "packageType")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<DeliveryPackage> deliveryPackages = new HashSet<>();

    public PackageType getOTHER() {
        return OTHER;
    }

    public PackageType getFRAGILE() {
        return FRAGILE;
    }

    public PackageType getELECTRONICS() {
        return ELECTRONICS;
    }

    public PackageType getCLOTHING() {
        return CLOTHING;
    }

    public PackageType getFOOD() {
        return FOOD;
    }

    @PostLoad
    public void postLoad(){
        packageTypeMapById.put(id, this);
        packageTypeMapByName.put(name, this);
        FOOD = getTypeById(1l);
        CLOTHING = getTypeById(2l);
        ELECTRONICS = getTypeById(3l);
        FRAGILE = getTypeById(4l);
        OTHER = getTypeById(5l);
        System.out.println("PackageType database called");
    }

    public static PackageType getTypeById(long id){
        return packageTypeMapById.get(id);
    }
    public static PackageType getTypeByName(String name){return packageTypeMapByName.get(name);}
}
