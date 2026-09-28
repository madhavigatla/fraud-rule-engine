package com.capitec.fraudengine.rules;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class OffHoursRule implements FraudRule {

    @Override
    public Optional<FraudAlert> evaluate(Transaction transaction) {
        if (transaction.getTimestamp() != null) {
            int hour = transaction.getTimestamp().getHour();
            if (hour >= 0 && hour <= 4) {
                return Optional.of(FraudAlert.builder()
                        .transactionId(transaction.getTransactionId())
                        .customerId(transaction.getCustomerId())
                        .ruleViolated(getRuleName())
                        .severity("LOW")
                        .alertTimestamp(LocalDateTime.now())
                        .build());
            }
        }
        return Optional.empty();
    }

    @Override
    public String getRuleName() {
        return "OFF_HOURS_TRANSACTION";
    }
}
