package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.model.SearchFilter;
import com.delix.deliveryou.spring.model.SearchFilterType;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.extender.UserRepositoryExtender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("from User where id = :id")
    User getUserById(@Param("id") Long id);

    @Query("from User where phone = :phone")
    User getUserByPhone(@Param("phone") String phone);

    boolean existsUserByPhone(String phone);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id and u.role.id = 1")
    boolean isUser(@Param("id") long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id and u.role.id = 2")
    boolean isShipper(@Param("id") long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id and u.role.id = 3")
    boolean isAdmin(@Param("id") long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id and u.role.id = :id_type")
    boolean assertRole(@Param("id") long userId, @Param("id_type") UserRole type);

    @Transactional
    @Modifying
    @Query("update User u set u.deleted = :deleted where u.id = :id")
    int markUserAsDeleted(@Param("id") long userId, @Param("deleted") boolean deleted);


//    @Modifying
//    @Transactional
//    @Query("UPDATE User SET phone = :userId, password = :new_password WHERE id = :user_id RETURNING CASE WHEN rowcount > 0 THEN TRUE ELSE FALSE END")
//    boolean updateUser(@Param("userId") User originalUser);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM User WHERE id = :userId) THEN TRUE ELSE FALSE END")
    boolean idExists(@Param("userId") long userId);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM User WHERE phone = :phone) THEN TRUE ELSE FALSE END")
    boolean phoneExists(@Param("phone") String phone);

    @Query("SELECT password FROM User WHERE id = :userId")
    String getPasswordHash(@Param("userId") long userId);

    @Query("select u from User u" +
            " where u.role.id = 2" +
            " and u.matchingReferences.minimumDeliveryPrice <= :price" +
            " and u.matchingReferences.maximumDeliveryDistance >= :distance" +
            " and not exists (select p from DeliveryPackage p where p.shipper.id = u.id and p.status.id = 4)")
    List<User> getPotentialDrivers(@Param("price") double minPrice, @Param("distance") double maxDistance);

    @Query("select d from User d where d.role.name = 'SHIPPER' and d.phone = :phone")
    List<User> getDriverWithPhone(@Param("phone") String phone);

    @Query("select count(u) from User u where u.role.id = :roleId")
    int countSystemUsersWithRole(@Param("roleId") long roleId);
}
