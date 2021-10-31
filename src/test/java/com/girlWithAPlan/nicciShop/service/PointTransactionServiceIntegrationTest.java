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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class PointTransactionServiceIntegrationTest {

    private static final BigDecimal POINT_AMOUNT = new BigDecimal("20.2003");

    private static Clock clock;
    private static PointTransaction newPointTransaction;
    private static Shopper shopper;

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

        shopper = Shopper.builder()
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
                        .build();
    }

    @Test
    public void createNewPointTransaction_whenShopperIsValidWithBalanceZero_returnsPointTransaction() {
        // given
        // assuming Shopper already exist
        Shopper shopper1 = shopperRepository.save(shopper);

        shopper1.setBalance(new BigDecimal("0.0000"));

        // when
        PointTransaction result = pointTransactionService.createNewPointTransaction(newPointTransaction, shopper1.getId());

        // then
        assertThat(result.getPointAmount(), is(equalTo(POINT_AMOUNT)));
        assertThat(result.getShopper(), is(equalTo(shopper1)));
        assertThat(result.getShopper().getId(), is(equalTo(shopper1.getId())));
        assertThat(result.getShopper().getBalance(), is(equalTo(POINT_AMOUNT)));
        assertThat(result.getStatus(), is(equalTo(TransactionStatus.COMPLETED)));
        assertThat(result.getCreatedAt(), is(equalTo(LocalDateTime.now(clock))));
    }

    @Test
    public void createNewPointTransaction_whenShopperIsValidWithBalance240_returnsPointTransaction() {
        // given
        // assuming Shopper already exist
        Shopper shopper1 = shopperRepository.save(shopper);

        BigDecimal oldBalance = new BigDecimal("240.0000");
        shopper1.setBalance(oldBalance);

        // when
        PointTransaction result = pointTransactionService.createNewPointTransaction(newPointTransaction, shopper1.getId());

        // then
        assertThat(result.getPointAmount(), is(equalTo(POINT_AMOUNT)));
        assertThat(result.getShopper(), is(equalTo(shopper1)));
        assertThat(result.getShopper().getId(), is(equalTo(shopper1.getId())));
        assertThat(result.getShopper().getBalance(), is(equalTo(oldBalance.add(POINT_AMOUNT))));
        assertThat(result.getStatus(), is(equalTo(TransactionStatus.COMPLETED)));
        assertThat(result.getCreatedAt(), is(equalTo(LocalDateTime.now(clock))));
    }

    @Test
    public void createNewPointTransaction_whenShopperIsNotValid_throwsNoSuchElementException() {
        // given

        // when
        NoSuchElementException result = assertThrows(NoSuchElementException.class,
                () -> pointTransactionService.createNewPointTransaction(newPointTransaction, 3L));

        // then
        assertThat(result.getMessage(), is(equalTo("Shopper not found for id= " + 3)));
    }

    @Test
    public void createNewPointTransaction_whenTransactionStatusIsREFUNDED_throwsIllegalArgumentException() {
        // given
        // assuming Shopper already exist
        Shopper shopper1 = shopperRepository.save(shopper);

        newPointTransaction.setStatus(TransactionStatus.REFUNDED);

        // when
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> pointTransactionService.createNewPointTransaction(newPointTransaction, shopper1.getId()));

        // then
        assertThat(result.getMessage(),
                    is(equalTo("TransactionStatus cannot be " + TransactionStatus.REFUNDED)));
    }

    @Test
    public void getPointTransactionsByShopperIdAndDate_whenAllTransactionsAreWithinDateRange_returnsListOfPointTransactions() {
        // given
        // assuming Shopper already exist
        Shopper shopper1 = shopperRepository.save(shopper);

        PointTransaction pointTransaction1 = PointTransaction.builder()
                .pointAmount(POINT_AMOUNT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.of(2020, 4, 13, 11, 34, 12))
                .shopper(shopper1)
                .build();

        PointTransaction pointTransaction2 = PointTransaction.builder()
                .pointAmount(POINT_AMOUNT.add(new BigDecimal("20")))
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.of(2020, 6, 17, 11, 34, 12))
                .shopper(shopper1)
                .build();

        PointTransaction pointTransaction3 = PointTransaction.builder()
                .pointAmount(POINT_AMOUNT.add(new BigDecimal("10.34")))
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.of(2021, 3, 24, 11, 34, 12))
                .shopper(shopper1)
                .build();

        List<PointTransaction> pointTransactionList = Arrays.asList(pointTransaction1, pointTransaction2, pointTransaction3);
        pointTransactionRepository.saveAll(pointTransactionList);

        // when
        List<PointTransaction> result = pointTransactionService.getPointTransactionsByShopperIdAndDateRange(shopper1.getId(),
                                                                            LocalDate.of(2020, 4, 13),
                                                                            LocalDate.of(2021, 3, 24));

        // then
        assertThat(result.size(), is(equalTo(pointTransactionList.size())));
    }
}
