package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
