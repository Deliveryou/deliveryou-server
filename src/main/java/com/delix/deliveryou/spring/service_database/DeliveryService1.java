package com.delix.deliveryou.spring.service_database;

import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.repository_database.DeliveryPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService1 {
    @Autowired
    private DeliveryPackageRepository packageRepository;

    public boolean savePackage(DeliveryPackage deliveryPackage) {
        try {
            packageRepository.save(deliveryPackage);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param newDeliveryPackage
     * @return an updated object, null if fail
     */
    public DeliveryPackage updatePackage(DeliveryPackage newDeliveryPackage) {
        return newDeliveryPackage;
    }

    public DeliveryPackage getPackage(long deliveryPackageId) {
        return packageRepository.getDeliveryPackageById(deliveryPackageId);
    }

    public boolean verifyPackage(long id) {
        return !(id < 1 || id > packageRepository.count());
    }

    public DeliveryPackage getActivePackage(long userId) {
        if (!verifyPackage(userId))
            throw new HttpBadRequestException();
        return packageRepository.getActivePackage(userId);
    }


}
