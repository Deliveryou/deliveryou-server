package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryPackageRepository extends JpaRepository<DeliveryPackage, Long> {
    @Query("from DeliveryPackage where id = :id")
    DeliveryPackage getDeliveryPackageById(@Param("id") Long id);

    @Query("SELECT d FROM DeliveryPackage d where (d.status.id = 4 or d.status.id = 3) AND (d.user.id = :id OR d.shipper.id = :id)")
    DeliveryPackage getActivePackage(@Param("id") long userId);

    @Query("SELECT d FROM DeliveryPackage d where d.user.id = :id OR d.shipper.id = :id")
    DeliveryPackage getCurrentPackage(@Param("id") long userId);

    @Query("from DeliveryPackage where user.id = :id")
    List<DeliveryPackage> getAllPackages(@Param("id") long userId);

//    boolean savePackage(DeliveryPackage deliveryPackage);
//
//    DeliveryPackage updatePackage(DeliveryPackage newDeliveryPackage);

    @Query("from DeliveryPackage where id = :id")
    DeliveryPackage getPackage(@Param("id") long deliveryPackageId);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM DeliveryPackage WHERE id = :id) THEN TRUE ELSE FALSE END")
    boolean verifyPackage(@Param("id") long id);

    @Query("select case when exists (select dp from DeliveryPackage dp where dp.id = :packageId and dp.shipper is null) then true else false end")
    boolean canAssignDriver(@Param("packageId") long packageId);

    @Modifying
    @Transactional
    @Query("update DeliveryPackage set status.id = 1 where id = :id")
    int cancelPackage(@Param("id") long packageId);

    @Modifying
    @Transactional
    @Query("update DeliveryPackage set status.id = 2 where id = :id")
    int finishDelivering(@Param("id") long packageId);

    @Modifying
    @Transactional
    @Query("update DeliveryPackage set status.id = 4 where id = :id")
    int confirmPickup(@Param("id") long packageId);

}
