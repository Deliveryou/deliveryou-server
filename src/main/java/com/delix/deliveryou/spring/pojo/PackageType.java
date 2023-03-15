package com.delix.deliveryou.spring.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PackageType {
    public static final PackageType FOOD = new PackageType(1l, "Food");
    public static final PackageType CLOTHING = new PackageType(2l, "Clothing");
    public static final PackageType ELECTRONICS = new PackageType(3l, "Electronics");
    public static final PackageType FRAGILE = new PackageType(4l, "Fragile");
    public static final PackageType OTHER = new PackageType(5l, "Other");


    private long id;
    private String name;
}
