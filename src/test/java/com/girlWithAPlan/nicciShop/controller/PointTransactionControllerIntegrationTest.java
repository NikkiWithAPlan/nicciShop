package com.girlWithAPlan.nicciShop.controller;

import com.girlWithAPlan.nicciShop.entity.Address;
import com.girlWithAPlan.nicciShop.entity.PointTransaction;
import com.girlWithAPlan.nicciShop.entity.Shopper;
import com.girlWithAPlan.nicciShop.entity.TransactionStatus;
import com.girlWithAPlan.nicciShop.service.PointTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PointTransactionControllerIntegrationTest {

    private static final BigDecimal POINT_AMOUNT = new BigDecimal("15.2031");

    @MockBean
    private PointTransactionService pointTransactionServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createNewPointTransaction_returnsPointTransaction() throws Exception {
        // given
        Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        Shopper shopper = Shopper.builder()
                                .firstName("Lily")
                                .lastName("Lilium")
                                .email("Lily@Lilium.com")
                                .dateOfBirth(LocalDate.of(1974, Month.FEBRUARY, 12))
                                .address(Address.builder()
                                        .addressLine("87 Fox Lane")
                                        .city("BLOUNT'S GREEN")
                                        .postCode("ST14 3GH")
                                        .country("United Kingdom")
                                        .build())
                                .build();

        PointTransaction pointTransaction = PointTransaction.builder()
                .pointAmount(POINT_AMOUNT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now(clock))
                .shopper(shopper)
                .build();

        given(pointTransactionServiceMock.createNewPointTransaction(eq(pointTransaction), anyLong()))
                .willReturn(pointTransaction);

        // when // then
        mockMvc.perform(post("/api/createPointTransaction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("pointAmount").value(POINT_AMOUNT))
                .andExpect(jsonPath("status").value(TransactionStatus.COMPLETED))
                .andExpect(jsonPath("createdAt").value(LocalDateTime.now(clock)));
    }
}
