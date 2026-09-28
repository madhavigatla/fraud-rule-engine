package com.capitec.fraudengine.rules;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VelocityRule implements FraudRule {

    private final Map<String, List<LocalDateTime>> customerTransactionHistory = new ConcurrentHashMap<>();

    @Value("${app.rules.velocity-threshold:3}")
    private int threshold;

    @Value("${app.rules.velocity-window-minutes:5}")
    private int windowMinutes;

    @Override
    public Optional<FraudAlert> evaluate(Transaction transaction) {
        if (isHighVelocity(transaction)) {
            return Optional.of(FraudAlert.builder()
                    .transactionId(transaction.getTransactionId())
                    .customerId(transaction.getCustomerId())
                    .ruleViolated(getRuleName())
                    .severity("HIGH")
                    .alertTimestamp(LocalDateTime.now())
                    .build());
        }
        return Optional.empty();
    }

    private boolean isHighVelocity(Transaction transaction) {
        String customerId = transaction.getCustomerId();
        if (customerId == null) return false;
        
        // Use transaction timestamp for velocity if available, otherwise current time
        LocalDateTime txTime = transaction.getTimestamp() != null ? transaction.getTimestamp() : LocalDateTime.now();
        
        List<LocalDateTime> history = customerTransactionHistory.computeIfAbsent(customerId, k -> new ArrayList<>());
        
        synchronized (history) {
            history.add(txTime);
            history.removeIf(time -> time.isBefore(txTime.minusMinutes(windowMinutes)));
            return history.size() > threshold;
        }
    }

    @Override
    public String getRuleName() {
        return "HIGH_VELOCITY_TRANSACTION";
    }
}
