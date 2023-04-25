package com.delix.deliveryou.spring.repository_database;

import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(Long id);
    User getUserByPhone(String phone);
    boolean existsUserByPhone(String phone);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id and u.role.id = 1")
    boolean isUser(@Param("id") long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id and u.role.id = 2")
    boolean isShipper(@Param("id") long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id and u.role.id = 3")
    boolean isAdmin(@Param("id") long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id and u.role.id = :id_type")
    boolean assertRole(@Param("id") long userId, @Param("id_type") UserRole type);
}
