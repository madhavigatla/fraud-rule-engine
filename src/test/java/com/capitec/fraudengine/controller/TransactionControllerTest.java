package com.capitec.fraudengine.controller;

import com.capitec.fraudengine.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StreamBridge streamBridge;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSendTransaction() throws Exception {
        Transaction transaction = Transaction.builder()
                .transactionId("TX123")
                .customerId("CUST001")
                .amount(new BigDecimal("100.00"))
                .timestamp(LocalDateTime.now())
                .build();

        when(streamBridge.send(eq("transaction-out-0"), any(Transaction.class))).thenReturn(true);

        mockMvc.perform(post("/api/transactions/post-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction sent successfully"));
    }
}
