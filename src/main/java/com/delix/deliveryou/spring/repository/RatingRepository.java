package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT CASE WHEN NOT EXISTS (SELECT r FROM Rating r WHERE r.deliveryPackage.id = :packageId) THEN TRUE ELSE FALSE END")
    boolean canRateShipper(@Param("packageId") long packageId);

    @Query("select count(r) from Rating r where r.deliveryPackage.shipper.id = :shipperId")
    long countRatingOfShipper(@Param("shipperId") long shipperId);

    @Query("select r from Rating r order by r.date desc")
    List<Rating> getAll();

    @Query("select r from Rating r where r.id = :id")
    Rating getById(@Param("id") long ratingId);
}
