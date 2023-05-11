package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageTypeRepository extends JpaRepository<PackageType, Long> {
}
