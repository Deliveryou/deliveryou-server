package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.pojo.Promotion;
import com.delix.deliveryou.spring.repository.PromotionRepository;
import com.delix.deliveryou.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PromotionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PromotionRepository promotionRepository;

    public boolean canApplyPromotion(long userId, long promotionId) {
        if (!userRepository.isUser(userId))
            return false;

        if (promotionRepository.doesPromotionExist(promotionId))
            return promotionRepository.canApplyPromotion(userId, promotionId);
        return false;
    }

    public List<Promotion> getAllPromotion() {
        return promotionRepository.getAllPromotion();
    }

    public List<Promotion> getApplicablePromotion(long userId) {
        if (!userRepository.isUser(userId))
            return Collections.emptyList();

        return promotionRepository.getAllPromotion()
                .stream()
                .filter(promotion -> canApplyPromotion(userId, promotion.getId()))
                .toList();
    }
}
