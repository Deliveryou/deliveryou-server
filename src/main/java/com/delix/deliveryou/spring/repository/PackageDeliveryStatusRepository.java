package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.PackageDeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageDeliveryStatusRepository extends JpaRepository<PackageDeliveryStatus, Integer> {
}
