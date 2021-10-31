package com.girlWithAPlan.nicciShop.repository;

import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PointTransaction
 */
@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    /**
     * Finds all the Point Transactions for a Shopper
     *
     * @param shopperId     which identifies the Shopper
     * @return              a list of Point Transactions
     */
    List<PointTransaction> findAllPointTransactionsByShopperId(Long shopperId);
}
