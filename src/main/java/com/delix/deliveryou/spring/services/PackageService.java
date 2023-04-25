package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.repository.DeliveryPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PackageService {
    @Autowired
    private DeliveryPackageRepository packageRepository;

    public List<DeliveryPackage> getAllPackages(long userId) {
        if (userId < 1)
            return Collections.emptyList();

        return packageRepository.getAllPackages(userId);
    }
}
