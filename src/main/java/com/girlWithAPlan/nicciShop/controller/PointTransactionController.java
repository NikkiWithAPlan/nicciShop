package com.girlWithAPlan.nicciShop.controller;

import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.service.PointTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.List;
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
        LOGGER.info("POST request was submitted to create new PointTransaction= {}", pointTransaction);

        if (pointTransaction.getShopper() == null) {
            LOGGER.warn("PointTransaction cannot be created, reason= Shopper must exist for PointTransaction={}", pointTransaction);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Long shopperId = pointTransaction.getShopper().getId();

        try {
            ResponseEntity<PointTransaction> responseEntity =
                    new ResponseEntity<>(pointTransactionService.createNewPointTransaction(pointTransaction, shopperId),
                                        HttpStatus.CREATED);

            LOGGER.info("New PointTransaction has been successfully created, {}", pointTransaction);

            return responseEntity;
        } catch (NoSuchElementException | IllegalArgumentException e) {
            LOGGER.warn("PointTransaction cannot be created, reason= {}, PointTransaction= {}", e.getMessage(), pointTransaction);
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("pointTransactions/{shopperId}/{startDate}/{endDate}")
    @ResponseStatus(HttpStatus.OK)
    public List<PointTransaction> getPointTransactionsByShopperIdAndDate(
            @PathVariable(value = "shopperId") Long shopperId,
            @PathVariable(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") @Past LocalDate startDate,
            @PathVariable(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") @PastOrPresent LocalDate endDate) {

        LOGGER.info("GET request was submitted to retrieve all PointTransactions by shopperId= {} , startDate= {} and endDate= {}",
                shopperId, startDate, endDate);

        return pointTransactionService.getPointTransactionsByShopperIdAndDateRange(shopperId, startDate, endDate);
    }
}