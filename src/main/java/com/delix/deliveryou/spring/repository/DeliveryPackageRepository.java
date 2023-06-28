package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.model.DistributionData;
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

    @Query("from DeliveryPackage where user.id = :id or shipper.id = :id")
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

    @Query("select dp.creationDate, count(dp) from DeliveryPackage dp where dp.shipper.id = :shipperId and month(dp.creationDate) = :month and year (dp.creationDate) = :year")
    List<Object[]> getAllPackagesInMonth(@Param("month") int month, @Param("year") int year, @Param("shipperId") long shipperId);

    @Query(" SELECT " +
            " (CASE WHEN SUM(p.price / (1 - COALESCE(pr.discountPercentage, 0)) * 0.8) IS NULL " +
            " THEN 0 ELSE SUM(p.price / (1 - COALESCE(pr.discountPercentage, 0)) * 0.8) END)" +
            " FROM DeliveryPackage p" +
            " JOIN p.shipper s" +
            " LEFT JOIN p.promotion pr" +
            " WHERE s.id = :shipperId " +
            " AND p.status.id = 2" +
            " AND MONTH(p.creationDate) = :smonth " +
            " AND YEAR(p.creationDate) = :syear")
    double shipperRevenueInMonth(@Param("shipperId") long shipperId, @Param("smonth") int month, @Param("syear") int year);

    @Query("select " +
            " (case when sum (dp.price / coalesce (1 - p.discountPercentage, 1) * 0.2 - coalesce ((dp.price / coalesce (1 - p.discountPercentage, 1)) * p.discountPercentage, 0)) is null" +
            " then 0 else sum (dp.price / coalesce (1 - p.discountPercentage, 1) * 0.2 - coalesce ((dp.price / coalesce (1 - p.discountPercentage, 1)) * p.discountPercentage, 0)) end)" +
            "   from DeliveryPackage dp" +
            "   left join dp.promotion p" +
            "   where month (dp.creationDate) = :smonth" +
            "   and year (dp.creationDate) = :syear" +
            "   and dp.status.id = 2")
    double systemRevenuePerMonth(@Param("smonth") int month, @Param("syear") int year);

    @Query("select count(dp) from DeliveryPackage dp where (dp.shipper.id = :userId or dp.user.id = :userId) and month(dp.creationDate) = :smonth and year(dp.creationDate) = :syear")
    int totalPackagesOfMonth(@Param("userId") long userId, @Param("smonth") int month, @Param("syear") int year);

    @Query("select count(dp) from DeliveryPackage dp where (dp.shipper.id = :userId or dp.user.id = :userId)")
    int allTimePackages(@Param("userId") long userId);

    @Query("select sum(dp.price) from DeliveryPackage dp where dp.user.id = :userId and dp.status.id = 2 and month(dp.creationDate) = :smonth and year(dp.creationDate) = :syear")
    int totalSpendingThisMonth(@Param("userId") long userId, @Param("smonth") int month, @Param("syear") int year);

    @Query("select sum(dp.price) from DeliveryPackage dp where dp.user.id = :userId and dp.status.id = 2")
    int allTimeSpending(@Param("userId") long userId);

    @Query("select count(dp) from DeliveryPackage dp")
    int countAllPackages();

    @Query("select dp.packageType.name, count(dp.id) from DeliveryPackage dp group by dp.packageType.name")
    List<Object[]> categoryDistribution();

    @Modifying
    @Transactional
    @Query("delete DeliveryPackage where id = :id")
    int delete(@Param("id") long id);
}