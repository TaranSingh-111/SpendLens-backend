package com.example.expense_tracker.dto;

import java.math.BigDecimal;

public record BudgetRequest(
        BigDecimal amount
) {
}
