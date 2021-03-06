package com.girlWithAPlan.nicciShop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.entity.TransactionStatus;
import com.girlWithAPlan.nicciShop.service.PointTransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointTransactionController.class)
public class PointTransactionControllerIntegrationTest {

    private static final BigDecimal POINT_AMOUNT = new BigDecimal("15.2031");
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    private static PointTransaction newPointTransaction;

    @MockBean
    private PointTransactionService pointTransactionServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setup() {
        newPointTransaction = PointTransaction.builder()
                .pointAmount(POINT_AMOUNT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now(CLOCK))
                .build();
    }

    @Test
    public void createNewPointTransaction_returnsPointTransaction() throws Exception {
        // given
        given(pointTransactionServiceMock.createNewPointTransaction(any(PointTransaction.class), anyLong()))
                .willReturn(newPointTransaction);

        // when
        String result = mockMvc.perform(post("/api/createPointTransaction/{id}", 1)
                        .content(getAsJsonString(newPointTransaction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(getPointTransaction(result).getPointAmount(), is(equalTo(newPointTransaction.getPointAmount())));
        assertThat(getPointTransaction(result).getCreatedAt(), is(equalTo(newPointTransaction.getCreatedAt())));
        assertThat(getPointTransaction(result).getStatus(), is(equalTo(newPointTransaction.getStatus())));
    }

    @Test
    public void createNewPointTransaction_whenShopperDoesNotExist_returnsUnprocessableEntityStatus() throws Exception {
        // given
        long shopperId = 20L;
        given(pointTransactionServiceMock.createNewPointTransaction(any(PointTransaction.class), anyLong()))
                .willThrow(new NoSuchElementException("Shopper not found for id=" + shopperId));

        // when // then
        mockMvc.perform(post("/api/createPointTransaction/{shopperId}", shopperId)
                        .content(getAsJsonString(newPointTransaction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createNewPointTransaction_whenTransactionStatusIsREFUNDED_returnsUnprocessableEntityStatus() throws Exception {
        // given
        newPointTransaction.setStatus(TransactionStatus.REFUNDED);
        given(pointTransactionServiceMock.createNewPointTransaction(any(PointTransaction.class), anyLong()))
                .willThrow(new IllegalArgumentException("TransactionStatus cannot be " + newPointTransaction.getStatus()));

        // when // then
        mockMvc.perform(post("/api/createPointTransaction/{id}", 1)
                        .content(getAsJsonString(newPointTransaction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getPointTransactionsByShopperIdAndDate_whenAllTransactionsAreWithinDateRange_returnsListOfPointTransactions() throws Exception {
        // given
        PointTransaction pointTransaction = newPointTransaction;
        pointTransaction.setStatus(TransactionStatus.COMPLETED);
        pointTransaction.setCreatedAt(LocalDateTime.of(2021, 1, 14, 16, 35, 11));

        given(pointTransactionServiceMock.getPointTransactionsByShopperIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(List.of(pointTransaction));

        // when
        String result = mockMvc.perform(get("/api/pointTransactions/{id}/{startDate}/{endDate}",
                        1L,
                        LocalDate.of(2020, 4, 13),
                        LocalDate.of(2021, 3, 12))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(getPointTransactionList(result).get(0).getPointAmount(), is(equalTo(pointTransaction.getPointAmount())));
        assertThat(getPointTransactionList(result).get(0).getCreatedAt(), is(equalTo(pointTransaction.getCreatedAt())));
        assertThat(getPointTransactionList(result).get(0).getStatus(), is(equalTo(pointTransaction.getStatus())));
    }

    @Test
    public void getPointTransactionsByShopperIdAndDate_whenShopperNotFound_returnsUnprocessableEntityStatus() throws Exception {
        // given
        given(pointTransactionServiceMock.getPointTransactionsByShopperIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willThrow(NoSuchElementException.class);

        // when // then
        mockMvc.perform(get("/api/pointTransactions/{id}/{startDate}/{endDate}",
                        1L,
                        LocalDate.of(2020, 4, 13),
                        LocalDate.of(2021, 3, 12))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getPointTransactionsByShopperIdAndDate_whenStartDateIsAfterEndDate_returnsUnprocessableEntityStatus() throws Exception {
        // given
        given(pointTransactionServiceMock.getPointTransactionsByShopperIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .willThrow(IllegalArgumentException.class);

        // when // then
        mockMvc.perform(get("/api/pointTransactions/{id}/{startDate}/{endDate}",
                        1L,
                        LocalDate.of(2021, 4, 13),
                        LocalDate.of(2020, 3, 12))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    private String getAsJsonString(final Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private PointTransaction getPointTransaction(final String jsonString) {
        try {
            return MAPPER.readValue(jsonString, PointTransaction.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<PointTransaction> getPointTransactionList(final String jsonString) {
        try {
            return Arrays.asList(MAPPER.readValue(jsonString, PointTransaction[].class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
