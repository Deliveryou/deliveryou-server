package com.delix.deliveryou.spring.repository.implementation;

import com.delix.deliveryou.spring.model.SearchFilter;
import com.delix.deliveryou.spring.model.SearchFilterType;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.extender.UserRepositoryExtender;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class UserRepositoryExtenderImpl implements UserRepositoryExtender{
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<User> getUsersWithFilter(UserRole role, SearchFilter filter) {
        try {
            var criteriaBuilder = entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(User.class);
            var user = criteriaQuery.from(User.class);
            var isRole = criteriaBuilder.equal(user.<UserRole>get("role").get("id"), role.getId());

            var noFilterRun = new Object() { boolean value = true; };

            SearchFilterType.getEngine(filter)
                    .whenFilterByUserId((id) -> {
                        var isUser = criteriaBuilder.equal(user.get("id"), id);
                        criteriaQuery.where(isRole, isUser);
                        noFilterRun.value = false;
                    })
                    .whenFilterByName((name) -> {
                        var matchFirstName = criteriaBuilder.like(user.get("firstName"), "%" + name + "%");
                        var matchLastName = criteriaBuilder.like(user.get("lastName"), "%" + name + "%");
                        var matchFirstOrLast = criteriaBuilder.or(matchFirstName, matchLastName);
                        criteriaQuery.where(isRole, matchFirstOrLast);
                        noFilterRun.value = false;
                    })
                    .whenFilterByBirthYear((year) -> {
                        var yearOfBirth = criteriaBuilder.function("year", Integer.class, user.get("dateOfBirth"));
                        var isBirthYear = criteriaBuilder.equal(yearOfBirth, year);
                        criteriaQuery.where(isRole, isBirthYear);
                        noFilterRun.value = false;
                    })
                    .whenFilterByPhone(phone -> {
                        var matchPhone = criteriaBuilder.like(user.get("phone"), "%" + phone + "%");
                        criteriaQuery.where(isRole, matchPhone);
                        noFilterRun.value = false;

                    })
//                    .whenFilterIsInvalid(() -> {
//                        result.addAll(userMockData.stream().filter(user -> user.getRole().getId() == role.getId()).toList());
//                    });
                    .whenGetAll(() -> {
                        criteriaQuery.where(isRole);
                        noFilterRun.value = false;
                    })
                    .whenFilterByCitizenId(citizenId -> {
                        var likeCID = criteriaBuilder.like(user.get("citizenId"), "%" + citizenId + "%");
                        criteriaQuery.where(isRole, likeCID);
                        noFilterRun.value = false;
                    });

            if (noFilterRun.value)
                return Collections.emptyList();

            var query = entityManager.createQuery(criteriaQuery);
            var list = query.getResultList();
            return (list != null && list.size() > 0) ? list : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
