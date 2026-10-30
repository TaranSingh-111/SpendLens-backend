package com.example.expense_tracker.dto;

import java.math.BigDecimal;

public record BudgetResponse(
        Long id,
        Integer year,
        Integer month,
        BigDecimal amount
) {
}
