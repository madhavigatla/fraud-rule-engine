package com.capitec.fraudengine.repository;

import com.capitec.fraudengine.model.FraudAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {

    Page<FraudAlert> findByCustomerId(String customerId, Pageable pageable);
    Page<FraudAlert> findBySeverity(String severity, Pageable pageable);
    boolean existsByTransactionIdAndRuleViolated(String transactionId, String ruleViolated);
}
