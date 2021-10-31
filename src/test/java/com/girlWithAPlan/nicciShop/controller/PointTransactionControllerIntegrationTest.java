package com.girlWithAPlan.nicciShop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.girlWithAPlan.nicciShop.entity.Address;
import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.entity.Shopper;
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
import java.time.Month;
import java.time.ZoneId;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointTransactionController.class)
public class PointTransactionControllerIntegrationTest {

    private static final BigDecimal POINT_AMOUNT = new BigDecimal("15.2031");
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    private static PointTransaction pointTransaction;

    @MockBean
    private PointTransactionService pointTransactionServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setup() {
        Address address = Address.builder()
                .id(3L)
                .addressLine("87 Fox Lane")
                .city("BLOUNT'S GREEN")
                .postCode("ST14 3GH")
                .country("United Kingdom")
                .build();

        Shopper shopper = Shopper.builder()
                .id(3L)
                .firstName("Lily")
                .lastName("Lilium")
                .email("Lily@Lilium.com")
                .dateOfBirth(LocalDate.of(1974, Month.FEBRUARY, 12))
                .address(address)
                .build();

        pointTransaction = PointTransaction.builder()
                .pointTransactionId(1L)
                .pointAmount(POINT_AMOUNT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now(CLOCK))
                .shopper(shopper)
                .build();
    }

    @Test
    public void createNewPointTransaction_returnsPointTransaction() throws Exception {
        // given
        given(pointTransactionServiceMock.createNewPointTransaction(any(PointTransaction.class), anyLong()))
                .willReturn(pointTransaction);

        // when
        String result = mockMvc.perform(post("/api/createPointTransaction")
                                        .content(getAsJsonString(pointTransaction))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

        // then
        assertThat(getPointTransaction(result), is(equalTo(pointTransaction)));
    }

    @Test
    public void createNewPointTransaction_whenShopperDoesNotExist_returnsUnprocessableEntityStatus() throws Exception {
        // given
        given(pointTransactionServiceMock.createNewPointTransaction(any(PointTransaction.class), anyLong()))
                .willThrow(NoSuchElementException.class);

        // when // then
        mockMvc.perform(post("/api/createPointTransaction")
                        .content(getAsJsonString(pointTransaction))
                        .contentType(MediaType.APPLICATION_JSON)
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
}
