package com.girlWithAPlan.nicciShop.repository;

import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
}
