package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.Promotion;
import com.delix.deliveryou.spring.pojo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class PromotionRepository {

    private static final List<Promotion> promotionMockData = new ArrayList<>() {{
        add(new Promotion(1l, "BHD4JN898L", "First promo", 0.1f, 5000, 0, OffsetDateTime.now().plusDays(6)));
        add(new Promotion(2l, "HC7HGbL2Q3", "Second promo", 0.15f, 10000, 40000, OffsetDateTime.now().plusDays(6)));
        add(new Promotion(3l, "273HJS383H","Third promo", 0.05f, 6000, 0, OffsetDateTime.now().plusDays(6)));
    }};

    // user -> list of promo
    private static final Map<Long, List<Long>> user_promoMockData = new HashMap<>() {{
        put(1l, Arrays.asList(2l));
    }};

    public Promotion getPromotion(long id) {
        return promotionMockData.get((int) id);
    }

    public boolean doesPromotionExist(long promotionId) {
        try {
            promotionMockData.get((int)(promotionId - 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canApplyPromotion(long userId, long promotionId) {
        var promoList = user_promoMockData.get(userId);
        return (promoList == null || !promoList.contains(promotionId));
    }

    public List<Promotion> getAllPromotion() {
        return promotionMockData;
    }

}
