package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DeliveryPackageRepository {
    private Map<Long, DeliveryPackage> deliveryPackageMockData = new HashMap<>();

    public boolean savePackage(DeliveryPackage deliveryPackage) {
        deliveryPackage.setId(deliveryPackageMockData.size() + 1);
        deliveryPackageMockData.put(deliveryPackage.getId(), deliveryPackage);
        return true;
    }

    public DeliveryPackage getPackage(long deliveryPackageId) {
        return deliveryPackageMockData.get(deliveryPackageId);
    }

    public boolean verifyPackage(long id) {
        return !(id < 1 || id > deliveryPackageMockData.size());
    }
}
