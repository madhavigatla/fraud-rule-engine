package com.capitec.fraudengine.service;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import com.capitec.fraudengine.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudRuleEngine {

    private final FraudAlertRepository fraudAlertRepository;
    private final Map<String, List<LocalDateTime>> customerTransactionHistory = new ConcurrentHashMap<>();

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000.00");
    private static final int VELOCITY_THRESHOLD = 3;
    private static final int VELOCITY_WINDOW_MINUTES = 5;

    public List<FraudAlert> processTransaction(Transaction transaction) {
        log.info("Processing transaction: {}", transaction.getTransactionId());
        List<FraudAlert> alerts = new ArrayList<>();

        // Rule 1: High Value Transaction
        if (transaction.getAmount().compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            alerts.add(createAlert(transaction, "HIGH_VALUE_TRANSACTION", "HIGH"));
        }

        // Rule 2: Suspicious Category (example: "Gambling" or "Crypto")
        if ("Gambling".equalsIgnoreCase(transaction.getCategory()) || "Crypto".equalsIgnoreCase(transaction.getCategory())) {
            alerts.add(createAlert(transaction, "SUSPICIOUS_CATEGORY", "MEDIUM"));
        }

        // Rule 3: Midnight Transaction (Between 00:00 and 04:00)
        int hour = transaction.getTimestamp().getHour();
        if (hour >= 0 && hour <= 4) {
            alerts.add(createAlert(transaction, "OFF_HOURS_TRANSACTION", "LOW"));
        }

        // Rule 4: Velocity Check (Real-time suspicious activity)
        if (isHighVelocity(transaction)) {
            alerts.add(createAlert(transaction, "HIGH_VELOCITY_TRANSACTION", "HIGH"));
        }

        if (!alerts.isEmpty()) {
            fraudAlertRepository.saveAll(alerts);
            log.warn("Fraud alerts generated for transaction {}: {}", transaction.getTransactionId(), alerts.size());
        }

        return alerts;
    }

    private boolean isHighVelocity(Transaction transaction) {
        String customerId = transaction.getCustomerId();
        LocalDateTime now = LocalDateTime.now();
        
        customerTransactionHistory.computeIfAbsent(customerId, k -> new ArrayList<>());
        List<LocalDateTime> history = customerTransactionHistory.get(customerId);
        
        // Add current transaction time
        history.add(now);
        
        // Remove old transactions outside the window
        history.removeIf(time -> time.isBefore(now.minusMinutes(VELOCITY_WINDOW_MINUTES)));
        
        return history.size() > VELOCITY_THRESHOLD;
    }

    private FraudAlert createAlert(Transaction transaction, String rule, String severity) {
        return FraudAlert.builder()
                .transactionId(transaction.getTransactionId())
                .customerId(transaction.getCustomerId())
                .ruleViolated(rule)
                .severity(severity)
                .alertTimestamp(LocalDateTime.now())
                .build();
    }
}
