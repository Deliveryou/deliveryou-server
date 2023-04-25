package com.delix.deliveryou.spring.repository_database;

import com.delix.deliveryou.spring.pojo.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    boolean existsPromotionById(long promotionId);
}
