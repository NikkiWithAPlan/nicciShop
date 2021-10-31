package com.girlWithAPlan.nicciShop.repository;

import com.girlWithAPlan.nicciShop.entity.Shopper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Shopper
 */
@Repository
public interface ShopperRepository extends JpaRepository<Shopper, Long> {
}
