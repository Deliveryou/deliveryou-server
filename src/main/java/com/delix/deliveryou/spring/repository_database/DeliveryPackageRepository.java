package com.delix.deliveryou.spring.repository_database;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryPackageRepository extends JpaRepository<DeliveryPackage, Long> {
    DeliveryPackage getDeliveryPackageById(Long id);

    @Query("SELECT d FROM DeliveryPackage d where d.status = PackageDeliveryStatus .DELIVERING AND d.user = :id OR d.shipper = :id")
    DeliveryPackage getActivePackage(@Param("id") long userId);
}
