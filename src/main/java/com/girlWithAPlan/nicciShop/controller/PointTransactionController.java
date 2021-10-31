package com.girlWithAPlan.nicciShop.controller;

import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.service.PointTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class PointTransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PointTransactionController.class);

    private final PointTransactionService pointTransactionService;

    @Autowired
    public PointTransactionController(PointTransactionService pointTransactionService) {
        this.pointTransactionService = pointTransactionService;
    }

    @PostMapping("/createPointTransaction")
    public ResponseEntity<PointTransaction> createPointTransaction(@Valid @RequestBody PointTransaction pointTransaction) {
        LOGGER.info("POST new point transaction={}", pointTransaction);

        if (pointTransaction.getShopper() == null) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Long shopperId = pointTransaction.getShopper().getId();

        try {
            return new ResponseEntity<>(pointTransactionService.createNewPointTransaction(pointTransaction, shopperId),
                                        HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
