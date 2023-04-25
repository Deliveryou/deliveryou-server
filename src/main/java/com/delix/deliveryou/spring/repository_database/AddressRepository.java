package com.delix.deliveryou.spring.repository_database;

import com.delix.deliveryou.spring.pojo.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
