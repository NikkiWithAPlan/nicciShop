package com.girlWithAPlan.nicciShop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_transaction")
@Builder
@AllArgsConstructor
@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointTransactionId;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 4)
    private BigDecimal pointAmount;

    @NotNull
    private TransactionStatus status;

    @PastOrPresent
    private LocalDateTime createdAt;

    @PastOrPresent
    private LocalDateTime updatedAt;

}
