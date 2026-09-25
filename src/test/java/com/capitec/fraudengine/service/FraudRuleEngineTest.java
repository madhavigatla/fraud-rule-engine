package com.capitec.fraudengine.service;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import com.capitec.fraudengine.repository.FraudAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class FraudRuleEngineTest {

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @InjectMocks
    private FraudRuleEngine fraudRuleEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenHighValueTransaction_thenGenerateAlert() {
        Transaction transaction = Transaction.builder()
                .transactionId("TX123")
                .customerId("CUST001")
                .amount(new BigDecimal("15000.00"))
                .category("Groceries")
                .timestamp(LocalDateTime.of(2023, 10, 27, 10, 0))
                .build();

        List<FraudAlert> alerts = fraudRuleEngine.processTransaction(transaction);

        assertEquals(1, alerts.size());
        assertEquals("HIGH_VALUE_TRANSACTION", alerts.get(0).getRuleViolated());
        assertEquals("HIGH", alerts.get(0).getSeverity());
        verify(fraudAlertRepository, times(1)).saveAll(anyList());
    }

    @Test
    void whenSuspiciousCategory_thenGenerateAlert() {
        Transaction transaction = Transaction.builder()
                .transactionId("TX456")
                .customerId("CUST002")
                .amount(new BigDecimal("500.00"))
                .category("Gambling")
                .timestamp(LocalDateTime.of(2023, 10, 27, 10, 0))
                .build();

        List<FraudAlert> alerts = fraudRuleEngine.processTransaction(transaction);

        assertEquals(1, alerts.size());
        assertEquals("SUSPICIOUS_CATEGORY", alerts.get(0).getRuleViolated());
        assertEquals("MEDIUM", alerts.get(0).getSeverity());
        verify(fraudAlertRepository, times(1)).saveAll(anyList());
    }

    @Test
    void whenOffHoursTransaction_thenGenerateAlert() {
        Transaction transaction = Transaction.builder()
                .transactionId("TX789")
                .customerId("CUST003")
                .amount(new BigDecimal("100.00"))
                .category("Retail")
                .timestamp(LocalDateTime.of(2023, 10, 27, 2, 0)) // 2 AM
                .build();

        List<FraudAlert> alerts = fraudRuleEngine.processTransaction(transaction);

        assertEquals(1, alerts.size());
        assertEquals("OFF_HOURS_TRANSACTION", alerts.get(0).getRuleViolated());
        assertEquals("LOW", alerts.get(0).getSeverity());
        verify(fraudAlertRepository, times(1)).saveAll(anyList());
    }

    @Test
    void whenNormalTransaction_thenNoAlerts() {
        Transaction transaction = Transaction.builder()
                .transactionId("TX000")
                .customerId("CUST004")
                .amount(new BigDecimal("50.00"))
                .category("Groceries")
                .timestamp(LocalDateTime.of(2023, 10, 27, 14, 0))
                .build();

        List<FraudAlert> alerts = fraudRuleEngine.processTransaction(transaction);

        assertTrue(alerts.isEmpty());
        verify(fraudAlertRepository, never()).saveAll(anyList());
    }

    @Test
    void whenHighVelocity_thenGenerateAlert() {
        String customerId = "CUST_VELOCITY";
        
        // Send 3 transactions (under threshold of 3)
        for (int i = 0; i < 3; i++) {
            Transaction t = Transaction.builder()
                    .transactionId("TX_V" + i)
                    .customerId(customerId)
                    .amount(new BigDecimal("10.00"))
                    .category("Retail")
                    .timestamp(LocalDateTime.now())
                    .build();
            fraudRuleEngine.processTransaction(t);
        }
        
        // The 4th transaction should trigger velocity alert
        Transaction t4 = Transaction.builder()
                .transactionId("TX_V4")
                .customerId(customerId)
                .amount(new BigDecimal("10.00"))
                .category("Retail")
                .timestamp(LocalDateTime.now())
                .build();
        
        List<FraudAlert> alerts = fraudRuleEngine.processTransaction(t4);
        
        assertFalse(alerts.isEmpty());
        assertTrue(alerts.stream().anyMatch(a -> "HIGH_VELOCITY_TRANSACTION".equals(a.getRuleViolated())));
        verify(fraudAlertRepository, atLeastOnce()).saveAll(anyList());
    }
}
