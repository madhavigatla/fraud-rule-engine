package com.capitec.fraudengine.controller;

import com.capitec.fraudengine.model.Transaction;
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
    private final static String transactionProducer ="transactionEventProducer";

    @PostMapping("/post-transaction")
    public ResponseEntity<String> sendTransaction(@RequestBody Transaction transaction) {
        log.info("Sending transaction to rules engine: {}", transaction.getTransactionId());
        
        boolean sent = streamBridge.send(transactionProducer, transaction);
        
        if (sent) {
            return ResponseEntity.ok("Transaction sent successfully");
        } else {
            return ResponseEntity.internalServerError().body("Failed to send transaction");
        }
    }
}
