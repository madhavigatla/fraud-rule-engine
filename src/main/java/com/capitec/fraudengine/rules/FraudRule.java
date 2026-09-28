package com.capitec.fraudengine.rules;

import com.capitec.fraudengine.model.FraudAlert;
import com.capitec.fraudengine.model.Transaction;

import java.util.Optional;

public interface FraudRule {
    Optional<FraudAlert> evaluate(Transaction transaction);
    String getRuleName();
}
