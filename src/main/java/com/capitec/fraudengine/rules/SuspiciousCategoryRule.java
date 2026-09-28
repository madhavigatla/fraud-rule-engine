package com.capitec.fraudengine.rules;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class SuspiciousCategoryRule implements FraudRule {

    @Override
    public Optional<FraudAlert> evaluate(Transaction transaction) {
        if (transaction.getCategory() != null && 
            ("Gambling".equalsIgnoreCase(transaction.getCategory()) || "Crypto".equalsIgnoreCase(transaction.getCategory()))) {
            return Optional.of(FraudAlert.builder()
                    .transactionId(transaction.getTransactionId())
                    .customerId(transaction.getCustomerId())
                    .ruleViolated(getRuleName())
                    .severity("MEDIUM")
                    .alertTimestamp(LocalDateTime.now())
                    .build());
        }
        return Optional.empty();
    }

    @Override
    public String getRuleName() {
        return "SUSPICIOUS_CATEGORY";
    }
}
