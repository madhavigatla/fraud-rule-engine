package com.capitec.fraudengine.service;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import com.capitec.fraudengine.repository.FraudAlertRepository;
import com.capitec.fraudengine.rules.FraudRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudRuleEngine {

    private final FraudAlertRepository fraudAlertRepository;
    private final List<FraudRule> rules;

    public List<FraudAlert> processTransaction(Transaction transaction) {
        if (transaction == null) {
            log.error("Received null transaction");
            return new ArrayList<>();
        }

        log.info("Processing transaction: {} for customer: {}. Number of rules: {}", 
                transaction.getTransactionId(), transaction.getCustomerId(), rules.size());
        
        // Log all registered rules
        rules.forEach(r -> log.debug("Rule registered in engine: {}", r.getRuleName()));
        
        List<FraudAlert> alerts = new ArrayList<>();

        for (FraudRule rule : rules) {
            log.debug("Evaluating rule: {} for transaction: {}", rule.getRuleName(), transaction.getTransactionId());
            rule.evaluate(transaction).ifPresent(alert -> {
                log.info("Rule {} violated by transaction {}", rule.getRuleName(), transaction.getTransactionId());
                // Idempotency check: Don't save duplicate alerts for same transaction and rule
                if (!fraudAlertRepository.existsByTransactionIdAndRuleViolated(
                        transaction.getTransactionId(), rule.getRuleName())) {
                    alerts.add(alert);
                } else {
                    log.info("Duplicate alert for transaction {} and rule {} ignored", 
                            transaction.getTransactionId(), rule.getRuleName());
                }
            });
        }

        if (!alerts.isEmpty()) {
            fraudAlertRepository.saveAll(alerts);
            log.warn("Fraud alerts generated for transaction {}: {}", transaction.getTransactionId(), alerts.size());
        }

        return alerts;
    }
}
