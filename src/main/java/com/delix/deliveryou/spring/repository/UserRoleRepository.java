package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    List<UserRole> findAll();

    UserRole getUserRoleById(int id);
}
