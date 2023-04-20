package com.delix.deliveryou.spring.repository_database;

import com.delix.deliveryou.spring.pojo.PackageDeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageDeliveryStatusRepository extends JpaRepository<PackageDeliveryStatus, Integer> {
}
