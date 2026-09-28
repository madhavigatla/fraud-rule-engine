package com.capitec.fraudengine.kafka;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;
import com.capitec.fraudengine.service.FraudRuleEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TransactionConsumer {

    private final FraudRuleEngine fraudRuleEngine;

    @Bean
    public Consumer<Transaction> transactionEventConsumer() {
        return transaction -> {
            log.info("Received event for transaction: {}",
                    (transaction != null ? transaction.getTransactionId() : "NULL"));
            
            if (transaction == null) {
                log.error("Received null transaction object");
                return;
            }
            try {
                log.info("Beginning processing for transaction: {} from customer: {}",
                        transaction.getTransactionId(), transaction.getCustomerId());
                List<FraudAlert> alerts = fraudRuleEngine.processTransaction(transaction);
                log.info("Finished processing transaction {}. Generated {} alerts.",
                        transaction.getTransactionId(), alerts.size());
            } catch (Exception e) {
                log.error("Error processing transaction {}: {}",
                        transaction.getTransactionId(), e.getMessage(), e);
            }
        };
    }
}
