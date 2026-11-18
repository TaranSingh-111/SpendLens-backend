package com.example.expense_tracker.dto.analytics;

import java.math.BigDecimal;

public record SpendingSummaryDTO(
        BigDecimal totalSpent,
        BigDecimal dailyAverage,
        long TransactionCount,
        BigDecimal previousPeriodSpent,
        BigDecimal percentageChange
) {
}
