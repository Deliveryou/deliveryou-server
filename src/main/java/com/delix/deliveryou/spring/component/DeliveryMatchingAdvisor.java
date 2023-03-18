package com.delix.deliveryou.spring.component;

import com.delix.deliveryou.spring.services.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class matches shippers with preferred package
 */
@Component
public class DeliveryMatchingAdvisor {
    @Autowired
    private DeliveryService deliveryService;

    private final Deque<Long> pendingPackages;

    public DeliveryMatchingAdvisor() {
        this.pendingPackages = new ArrayDeque<>();
    }

    public boolean addPendingPackage(Long packageId) {
        if (!deliveryService.verifyPackage(packageId))
            return false;
        return pendingPackages.add(packageId);
    }

   public class MatchingService {

        public void performMatch() {
            try {
                if (pendingPackages.size() > 0) {
                    long packageId = pendingPackages.getFirst();
                    var deliveryPackage = deliveryService.getPackage(packageId);

                    //deliveryPackage.
                }
            } catch (Exception e) {

            }
        }
   }

}
