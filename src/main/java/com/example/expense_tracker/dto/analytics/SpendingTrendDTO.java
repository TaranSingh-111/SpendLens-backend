package com.example.expense_tracker.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SpendingTrendDTO(
        LocalDate date,
        BigDecimal amount
) {
}
