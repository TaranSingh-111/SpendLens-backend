package com.example.expense_tracker.dto.analytics;

import java.math.BigDecimal;

public record CategorySpendingDTO(
        String category,
        BigDecimal amount,
        BigDecimal percentage
) {
}
