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

        if (promotionRepository.doesPromotionExist(promotionId)) {
            try {
                var result = promotionRepository.canApplyPromotion(userId, promotionId);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<Promotion> getAllPromotion() {
        return promotionRepository.getAllPromotion();
    }

    public List<Promotion> getApplicablePromotion(long userId) {
        if (!userRepository.isUser(userId))
            return Collections.emptyList();

        var list  = promotionRepository.getAllPromotion();
        return list
                .stream()
                .filter(promotion -> canApplyPromotion(userId, promotion.getId()))
                .toList();
    }

    public Promotion loadPromotion(long id) {
        return promotionRepository.getPromotion(id);
    }

    public boolean canUsePromoCode(String code) {
        if (code == null)
            return false;
        code = code.trim();
        if (code.length() != 10)
            return false;
        return promotionRepository.canUsePromoCode(code);
    }

    public long addPromotion(Promotion promotion) {
        if (promotion == null || !canUsePromoCode(promotion.getPromoCode()))
            return 0;

        var saved = promotionRepository.save(promotion);
        return saved.getId(); // return id
    }

    public List<Promotion> searchForPromotions(String keywords) {
        return promotionRepository.searchForPromos(keywords);
    }
}
