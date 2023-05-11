package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM Promotion WHERE id = :id) THEN TRUE ELSE FALSE END")
    boolean doesPromotionExist(@Param("id") long promotionId);

    @Query("select count(dp) = 0 from DeliveryPackage dp where dp.user.id = :userId and dp.promotion.id = :promoId")
    boolean canApplyPromotion(@Param("userId") long userId, @Param("promoId") long promotionId);

    @Query("select p from Promotion p")
    List<Promotion> getAllPromotion();

    @Query("select p from Promotion p where p.id = :id")
    Promotion getPromotion(@Param("id") long id);
}
