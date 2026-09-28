package com.capitec.fraudengine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fraud_alerts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"transactionId", "ruleViolated"})
})
public class FraudAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String ruleViolated;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private LocalDateTime alertTimestamp;
}
