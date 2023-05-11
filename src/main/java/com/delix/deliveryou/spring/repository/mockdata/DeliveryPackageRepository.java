package com.delix.deliveryou.spring.repository.mockdata;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.pojo.PackageDeliveryStatus;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Repository
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

    public DeliveryPackage updatePackage(DeliveryPackage newDeliveryPackage) {
        try {
            if (deliveryPackageMockData.get(newDeliveryPackage.getId()) != null) {
                deliveryPackageMockData.put(newDeliveryPackage.getId(), newDeliveryPackage);
                return newDeliveryPackage;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public DeliveryPackage getActivePackage(long userId) {
        var list = deliveryPackageMockData.values();
        for (DeliveryPackage dp : list) {
            if (dp.getStatus() == PackageDeliveryStatus.DELIVERING && (dp.getUser().getId() == userId || dp.getShipper().getId() == userId)) {
                return dp;
            }
        }
        return null;
    }

    public List<DeliveryPackage> getAllPackages(long userId) {
        try {
            var list = deliveryPackageMockData.values().stream()
                            .filter(deliveryPackage -> deliveryPackage.getUser().getId() == userId)
                            .toList();
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
