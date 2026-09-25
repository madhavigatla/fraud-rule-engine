package com.capitec.fraudengine.controller;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud-alerts")
@RequiredArgsConstructor
public class FraudAlertController {

    private final FraudAlertRepository fraudAlertRepository;

    @GetMapping
    public ResponseEntity<List<FraudAlert>> getAllAlerts() {
        return ResponseEntity.ok(fraudAlertRepository.findAll());
    }

    @GetMapping("/transaction/{customerId}")
    public ResponseEntity<List<FraudAlert>> getAlertsByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(fraudAlertRepository.findByCustomerId(customerId));
    }
}
