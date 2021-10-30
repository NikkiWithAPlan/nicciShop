package com.girlWithAPlan.nicciShop.service;

import com.girlWithAPlan.nicciShop.entity.Address;
import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.entity.Shopper;
import com.girlWithAPlan.nicciShop.entity.TransactionStatus;
import com.girlWithAPlan.nicciShop.repository.PointTransactionRepository;
import com.girlWithAPlan.nicciShop.repository.ShopperRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class PointTransactionServiceIntegrationTest {

    public static final BigDecimal POINT_AMOUNT = new BigDecimal("20.2003");

    private static Clock clock;
    private static PointTransaction newPointTransaction;

    @Autowired
    private PointTransactionService pointTransactionService;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;
    @Autowired
    private ShopperRepository shopperRepository;

    @BeforeAll
    public static void setup() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

        newPointTransaction = PointTransaction.builder()
                .pointAmount(POINT_AMOUNT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now(clock))
                .build();
    }

    @Test
    public void createNewPointTransaction_whenShopperIsValid_returnsPointTransaction() {
        // given
        // assuming Shopper already exist
        Shopper shopper = shopperRepository.save(Shopper.builder()
                .firstName("Lily")
                .lastName("Lilium")
                .email("Lily@Lilium.com")
                .dateOfBirth(LocalDate.of(1974, Month.FEBRUARY, 12))
                .address(Address.builder()
                                .addressLine(any(String.class).toString())
                                .city(any(String.class).toString())
                                .postCode(any(String.class).toString())
                                .country(any(String.class).toString())
                                .build())
                .build());

        // when
        PointTransaction result = pointTransactionService.createNewPointTransaction(newPointTransaction, shopper.getId());

        // then
        assertThat(result.getPointAmount(), is(equalTo(POINT_AMOUNT)));
        assertThat(result.getStatus(), is(equalTo(TransactionStatus.COMPLETED)));
        assertThat(result.getShopper(), is(equalTo(shopper)));
        assertThat(result.getShopper().getId(), is(equalTo(shopper.getId())));
        assertThat(result.getCreatedAt(), is(equalTo(LocalDateTime.now(clock))));
    }

    @Test
    public void createNewPointTransaction_whenShopperIsNotValid_throwsNoSuchElementException() {
        // given

        // when
        NoSuchElementException result = assertThrows(NoSuchElementException.class,
                () -> pointTransactionService.createNewPointTransaction(newPointTransaction, 3L));

        // then
        assertThat(result.getMessage(), is(equalTo("Shopper not found for id=" + 3)));
    }
}
