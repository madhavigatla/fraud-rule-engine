package com.capitec.fraudengine.rules;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class HighValueRule implements FraudRule {

    @Value("${app.rules.high-value-threshold:10000.00}")
    private BigDecimal threshold;

    @Override
    public Optional<FraudAlert> evaluate(Transaction transaction) {
        if (transaction.getAmount() != null && transaction.getAmount().compareTo(threshold) > 0) {
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

    @Override
    public String getRuleName() {
        return "HIGH_VALUE_TRANSACTION";
    }
}
