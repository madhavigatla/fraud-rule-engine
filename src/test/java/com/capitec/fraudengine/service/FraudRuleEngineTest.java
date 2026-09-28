package com.capitec.fraudengine.service;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import com.capitec.fraudengine.repository.FraudAlertRepository;
import com.capitec.fraudengine.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class FraudRuleEngineTest {

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    private FraudRuleEngine fraudRuleEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        HighValueRule highValueRule = new HighValueRule();
        ReflectionTestUtils.setField(highValueRule, "threshold", new BigDecimal("10000.00"));
        
        VelocityRule velocityRule = new VelocityRule();
        ReflectionTestUtils.setField(velocityRule, "threshold", 3);
        ReflectionTestUtils.setField(velocityRule, "windowMinutes", 5);
        
        List<FraudRule> rules = Arrays.asList(
            highValueRule,
            new SuspiciousCategoryRule(),
            new OffHoursRule(),
            velocityRule
        );
        
        fraudRuleEngine = new FraudRuleEngine(fraudAlertRepository, rules);
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
        
        when(fraudAlertRepository.existsByTransactionIdAndRuleViolated(anyString(), anyString())).thenReturn(false);

        List<FraudAlert> alerts = fraudRuleEngine.processTransaction(transaction);

        assertEquals(1, alerts.size());
        assertEquals("HIGH_VALUE_TRANSACTION", alerts.get(0).getRuleViolated());
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
        String customerId = "CUST-VEL";
        when(fraudAlertRepository.existsByTransactionIdAndRuleViolated(anyString(), anyString())).thenReturn(false);

        // Send 3 transactions (threshold is 3, so 4th one should trigger)
        for (int i = 1; i <= 3; i++) {
            Transaction tx = Transaction.builder()
                    .transactionId("TX-V" + i)
                    .customerId(customerId)
                    .amount(new BigDecimal("10.00"))
                    .timestamp(LocalDateTime.now())
                    .build();
            fraudRuleEngine.processTransaction(tx);
        }

        // 4th transaction
        Transaction tx4 = Transaction.builder()
                .transactionId("TX-V4")
                .customerId(customerId)
                .amount(new BigDecimal("10.00"))
                .timestamp(LocalDateTime.now())
                .build();
        
        List<FraudAlert> alerts = fraudRuleEngine.processTransaction(tx4);

        assertEquals(1, alerts.size());
        assertEquals("HIGH_VELOCITY_TRANSACTION", alerts.get(0).getRuleViolated());
        verify(fraudAlertRepository, atLeastOnce()).saveAll(anyList());
    }
}
