package com.example.expense_tracker.dto.analytics;

import java.math.BigDecimal;

public record BudgetAnalysisDTO(
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal utilization
) {
}
