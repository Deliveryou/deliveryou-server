package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.DeliveryPackageRepository;
import com.delix.deliveryou.spring.repository.UserRepository;
import com.delix.deliveryou.spring.repository.extender.DeliveryPackageRepositoryExtender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PackageService {
    @Autowired
    private DeliveryPackageRepositoryExtender packageRepositoryExtender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryPackageRepository packageRepository;

    public List<DeliveryPackage> getAllPackages(long userId) {
        if (userId < 1)
            return Collections.emptyList();

        return packageRepository.getAllPackages(userId);
    }

    /**
     * @param packageId
     * @return true if the package has no assigned driver, false if cannot be assigned or [packageId] is invalid
     */
    public boolean canAssignDriver(long packageId) {
        return packageRepository.canAssignDriver(packageId);
    }

    public boolean cancelPackage(long packageId) {
        int result = packageRepository.cancelPackage(packageId);
        return (result > 0);
    }

    public boolean finishDelivering(long packageId) {
        int result = packageRepository.finishDelivering(packageId);
        return (result > 0);
    }

    public List<DeliveryPackage> packageHistory(long userId, int startIndex, int endIndex) {
        try {
            var user = userRepository.getUserById(userId);


            if (user.getRole().getId() == UserRole.USER.getId()) {

            } else if (user.getRole().getId() == UserRole.SHIPPER.getId()) {

            } else
                return Collections.emptyList();

            return packageRepositoryExtender.packageHistory(userId, startIndex, endIndex);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public DeliveryPackage getPackage(long packageId) {
        return packageRepository.getPackage(packageId);
    }

    public boolean confirmPickup(long packageId) {
        var result = packageRepository.confirmPickup(packageId);
        return result > 0;
    }

}
