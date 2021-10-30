package com.girlWithAPlan.nicciShop.service;

import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.entity.Shopper;
import com.girlWithAPlan.nicciShop.repository.PointTransactionRepository;
import com.girlWithAPlan.nicciShop.repository.ShopperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PointTransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PointTransaction.class);

    private final PointTransactionRepository pointTransactionRepository;
    private final ShopperRepository shopperRepository;

    @Autowired
    public PointTransactionService(PointTransactionRepository pointTransactionRepository,
                                   ShopperRepository shopperRepository) {
        this.pointTransactionRepository = pointTransactionRepository;
        this.shopperRepository = shopperRepository;
    }

    public PointTransaction createNewPointTransaction(PointTransaction pointTransaction, Long shopperId) {
        LOGGER.info("Create new PointTransaction={} for ShopperId={}", pointTransaction, shopperId);

        Shopper shopper = shopperRepository.findById(shopperId)
                .orElseThrow(() -> new NoSuchElementException("Shopper not found for id=" + shopperId));

        pointTransaction.setShopper(shopper);

        return pointTransactionRepository.save(pointTransaction);
    }

}
