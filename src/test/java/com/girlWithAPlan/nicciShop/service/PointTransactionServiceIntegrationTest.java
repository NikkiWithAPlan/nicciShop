package com.girlWithAPlan.nicciShop.service;

import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.entity.Shopper;
import com.girlWithAPlan.nicciShop.entity.TransactionStatus;
import com.girlWithAPlan.nicciShop.repository.PointTransactionRepository;
import com.girlWithAPlan.nicciShop.repository.ShopperRepository;
import org.exparity.hamcrest.date.LocalDateMatchers;
import org.exparity.hamcrest.date.LocalDateTimeMatchers;
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
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class PointTransactionServiceIntegrationTest {

    private static final BigDecimal POINT_AMOUNT = new BigDecimal("20.2003");

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
    public void createNewPointTransaction_whenShopperIsValidWithBalanceZero_returnsPointTransaction() {
        // given
        Shopper shopper1 = shopperRepository.getById(1L);

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
        Shopper shopper1 = shopperRepository.getById(1L);

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
                () -> pointTransactionService.createNewPointTransaction(newPointTransaction, 50L));

        // then
        assertThat(result.getMessage(), is(equalTo("Shopper not found for id= " + 50)));
    }

    @Test
    public void createNewPointTransaction_whenTransactionStatusIsREFUNDED_throwsIllegalArgumentException() {
        // given
        Shopper shopper1 = shopperRepository.getById(1L);

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
        Shopper shopper1 = shopperRepository.getById(1L);
        LocalDate startDate = LocalDate.of(2018, 4, 13);
        LocalDate endDate = LocalDate.of(2020, 2, 15);

        List<PointTransaction> pointTransactionList = pointTransactionRepository.findAllPointTransactionsByShopperId(1L);

        // when
        List<PointTransaction> result = pointTransactionService.getPointTransactionsByShopperIdAndDateRange(shopper1.getId(),
                                                                                                            startDate,
                                                                                                            endDate);

        // then
        assertThat(result,
                everyItem(hasProperty("createdAt", LocalDateTimeMatchers.after(LocalDateTime.of(startDate, LocalTime.MIDNIGHT)))));
        assertThat(result,
                everyItem(hasProperty("createdAt", LocalDateTimeMatchers.before(LocalDateTime.of(endDate, LocalTime.MIDNIGHT)))));
    }

    @Test
    public void getPointTransactionsByShopperIdAndDate_whenStartDateIsAfterEndDate_throwsIllegalArgumentException() {
        // given
        Shopper shopper1 = shopperRepository.getById(1L);
        LocalDate startDate = LocalDate.of(2021, 4, 13);
        LocalDate endDate = LocalDate.of(2020, 3, 24);

        // when
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> pointTransactionService.getPointTransactionsByShopperIdAndDateRange(shopper1.getId(),
                                                startDate, endDate));


        // then
        assertThat(result.getMessage(),
                is(equalTo("startDate= " + startDate + " cannot be after endDate= " + endDate)));
    }
}
