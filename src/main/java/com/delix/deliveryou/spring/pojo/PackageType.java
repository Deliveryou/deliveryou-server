package com.delix.deliveryou.spring.pojo;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PackageType {
    public static PackageType getTypeByName(String packageTypeName) {
        if (packageTypeName == null || packageTypeName.trim().length() < 4)
            return null;
        packageTypeName = packageTypeName.trim();
        packageTypeName = packageTypeName.toLowerCase();
        packageTypeName = String.valueOf(packageTypeName.charAt(0)).toUpperCase() + packageTypeName.substring(1);

        switch (packageTypeName) {
            case "Food": return FOOD;
            case "Clothing": return CLOTHING;
            case "Electronics": return ELECTRONICS;
            case "Fragile": return FRAGILE;
            case "Other": return OTHER;
            default: return null;
        }
    }
    public static final PackageType FOOD = new PackageType(1l, "Food");
    public static final PackageType CLOTHING = new PackageType(2l, "Clothing");
    public static final PackageType ELECTRONICS = new PackageType(3l, "Electronics");
    public static final PackageType FRAGILE = new PackageType(4l, "Fragile");
    public static final PackageType OTHER = new PackageType(5l, "Other");

    private long id;
    private String name;
}
