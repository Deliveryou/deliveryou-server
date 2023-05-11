package com.delix.deliveryou.spring.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Transient
    public static final PackageType FOOD = new PackageType(1, "FOOD");
    @Transient
    public static final PackageType CLOTHING = new PackageType(2, "CLOTHING");
    @Transient
    public static final PackageType ELECTRONICS = new PackageType(3, "ELECTRONICS");
    @Transient
    public static final PackageType FRAGILE = new PackageType(4, "FRAGILE");
    @Transient
    public static final PackageType OTHER = new PackageType(5, "OTHER");

    public PackageType(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "packageType")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<DeliveryPackage> deliveryPackages = new HashSet<>();

    public static PackageType getTypeByName(String name){
        if (name == null)
            return null;

        name = name.trim().toUpperCase();

        switch (name) {
            case "FOOD": return FOOD;
            case "CLOTHING": return CLOTHING;
            case "ELECTRONICS": return ELECTRONICS;
            case "FRAGILE": return FRAGILE;
            case "OTHER": return OTHER;
        }
        return null;
    }
}
