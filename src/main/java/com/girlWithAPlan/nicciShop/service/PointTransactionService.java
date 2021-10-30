package com.girlWithAPlan.nicciShop.service;

import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.repository.PointTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
public class PointTransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PointTransaction.class);

    private final PointTransactionRepository pointTransactionRepository;

    @Autowired
    public PointTransactionService(PointTransactionRepository pointTransactionRepository) {
        this.pointTransactionRepository = pointTransactionRepository;
    }

    public PointTransaction createNewPointTransaction(@Valid PointTransaction pointTransaction) {
        LOGGER.info("Create new PointTransaction={}", pointTransaction);
        return null;
    }

}
