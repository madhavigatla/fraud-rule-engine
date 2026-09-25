package com.capitec.fraudengine.kafka;

import com.capitec.fraudengine.model.Transaction;
import com.capitec.fraudengine.service.FraudRuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TransactionConsumer {

    private final FraudRuleEngine fraudRuleEngine;

    @Bean
    public Consumer<Transaction> consumeTransaction() {
        return transaction -> {
            log.info("Received transaction for processing: {}", transaction.getTransactionId());
            try {
                fraudRuleEngine.processTransaction(transaction);
            } catch (Exception e) {
                log.error("Error processing transaction {}: {}", transaction.getTransactionId(), e.getMessage());
            }
        };
    }
}
