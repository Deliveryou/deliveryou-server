package com.delix.deliveryou.spring.repository.implementation;

import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.UserRepository;
import com.delix.deliveryou.spring.repository.extender.DeliveryPackageRepositoryExtender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class DeliveryPackageRepositoryExtenderImpl implements DeliveryPackageRepositoryExtender {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<DeliveryPackage> packageHistory(long userId, int startIndex, int endIndex) {
        try {
            var user = userRepository.getUserById(userId);
            var obj = new Object() {
                 public List<DeliveryPackage> getList(String userType) {
                     userType = userType.trim();
                     return entityManager.createQuery("select dp from DeliveryPackage dp where dp." + userType + ".id = " + userId + " order by dp.creationDate desc", DeliveryPackage.class)
                             .setFirstResult(startIndex)
                             .setMaxResults(endIndex)
                             .getResultList();
                 }
             };

            if (user.getRole().getId() == UserRole.USER.getId())
                return obj.getList("user");

            if (user.getRole().getId() == UserRole.SHIPPER.getId())
                return obj.getList("shipper");

            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
