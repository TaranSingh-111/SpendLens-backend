package com.example.expense_tracker.dto.analytics;

import java.util.List;

public record AnalyticsResponse(
        SpendingSummaryDTO summary,
        List<SpendingTrendDTO> trends,
        List<CategorySpendingDTO> categories,
        BudgetAnalysisDTO budget,
        List<TopExpenseDTO> topExpenses
) {
}
