package com.delix.deliveryou.spring.repository.extender;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;

import java.util.List;

public interface DeliveryPackageRepositoryExtender {
    List<DeliveryPackage> packageHistory(long userId, int startIndex, int endIndex);
}
