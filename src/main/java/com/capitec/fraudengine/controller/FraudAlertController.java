package com.capitec.fraudengine.controller;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fraud-alerts")
@RequiredArgsConstructor
public class FraudAlertController {

    private final FraudAlertRepository fraudAlertRepository;

    @GetMapping
    public ResponseEntity<Page<FraudAlert>> getAllAlerts(
            @RequestParam(required = false) String severity,
            @PageableDefault(size = 20) Pageable pageable) {
        if (severity != null) {
            return ResponseEntity.ok(fraudAlertRepository.findBySeverity(severity, pageable));
        }
        return ResponseEntity.ok(fraudAlertRepository.findAll(pageable));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<FraudAlert>> getAlertsByCustomerId(
            @PathVariable String customerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(fraudAlertRepository.findByCustomerId(customerId, pageable));
    }
}
