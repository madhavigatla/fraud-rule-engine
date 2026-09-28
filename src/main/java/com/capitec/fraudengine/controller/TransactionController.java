package com.capitec.fraudengine.controller;

import com.capitec.fraudengine.model.Transaction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final StreamBridge streamBridge;
    private final static String TRANSACTION_PRODUCER = "transactionEventProducer-out-0";

    @PostMapping("/post-transaction")
    public ResponseEntity<String> sendTransaction(@Valid @RequestBody Transaction transaction) {
        log.info("Sending transaction to rules engine: {}", transaction.getTransactionId());
        
        try {
            boolean sent = streamBridge.send(TRANSACTION_PRODUCER, transaction);
            log.info("Transaction send result for {}: {}", transaction.getTransactionId(), sent);
            
            if (sent) {
                return ResponseEntity.ok("Transaction sent successfully");
            } else {
                log.error("Failed to send transaction: {}", transaction.getTransactionId());
                return ResponseEntity.internalServerError().body("Failed to send transaction");
            }
        } catch (Exception e) {
            log.error("Exception while sending transaction {}: {}", transaction.getTransactionId(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
