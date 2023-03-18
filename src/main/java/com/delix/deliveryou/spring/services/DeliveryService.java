package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.repository.DeliveryPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryPackageRepository packageRepository;

    public boolean savePackage(DeliveryPackage deliveryPackage) {
        return  packageRepository.savePackage(deliveryPackage);
    }

    public DeliveryPackage getPackage(long deliveryPackageId) {
        return packageRepository.getPackage(deliveryPackageId);
    }

    public boolean verifyPackage(long id) {
        return packageRepository.verifyPackage(id);
    }

}
