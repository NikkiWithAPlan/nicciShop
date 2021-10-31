package com.girlWithAPlan.nicciShop.repository;

import com.girlWithAPlan.nicciShop.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
