package com.girlWithAPlan.nicciShop.service;

import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.entity.TransactionStatus;
import com.girlWithAPlan.nicciShop.repository.PointTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class PointTransactionServiceIntegrationTest {

    public static final BigDecimal POINT_AMOUNT = new BigDecimal("20.2003");

    private static Clock clock;

    @Autowired
    private PointTransactionService pointTransactionService;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @BeforeAll
    public static void setup() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }

    @Test
    public void createNewPointTransaction_returnsPointTransaction() {
        // given
        PointTransaction newPointTransaction = PointTransaction.builder()
                .pointAmount(POINT_AMOUNT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now(clock))
                .build();

        // when
        PointTransaction result = pointTransactionService.createNewPointTransaction(newPointTransaction);

        // then
        assertThat(result.getPointAmount(), is(equalTo(POINT_AMOUNT)));
        assertThat(result.getStatus(), is(equalTo(TransactionStatus.COMPLETED)));
        assertThat(result.getCreatedAt(), is(equalTo(LocalDateTime.now(clock))));
    }
}
