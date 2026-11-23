package com.example.expense_tracker.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TopExpenseDTO(
        Integer id,
        String category,
        BigDecimal amount,
        String description,
        LocalDate expense_date
) {
}
