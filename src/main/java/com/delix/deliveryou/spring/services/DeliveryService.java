package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.repository.DeliveryPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryPackageRepository packageRepository;

    /**
     * save package
     * @param deliveryPackage
     * @return always true, otherwise throw an exception
     * @throws IllegalArgumentException
     * @throws org.springframework.dao.OptimisticLockingFailureException
     */
    public DeliveryPackage savePackage(DeliveryPackage deliveryPackage) {
        var entity = packageRepository.save(deliveryPackage);
        return entity;
    }

    /**
     * update package (save under)
     * @return non-null object, otherwise throw an exception
     * @throws IllegalArgumentException
     * @throws org.springframework.dao.OptimisticLockingFailureException
     */
    public DeliveryPackage updatePackage(DeliveryPackage newDeliveryPackage) {
        return packageRepository.save(newDeliveryPackage);
    }

    public DeliveryPackage getPackage(long deliveryPackageId) {
        return packageRepository.getPackage(deliveryPackageId);
    }

    public boolean verifyPackage(long id) {
        return packageRepository.verifyPackage(id);
    }

    /**
     * @param userId
     * @return package object, null if not exist
     * @throws HttpBadRequestException if id is invalid
     */
    public DeliveryPackage getActivePackage(long userId) {
        if (userId < 1)
            throw new HttpBadRequestException();
        return packageRepository.getActivePackage(userId);
    }

    public DeliveryPackage getCurrentPackage(long userId) {
        return packageRepository.getCurrentPackage(userId);
    }
}
