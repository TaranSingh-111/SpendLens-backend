package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.analytics.*;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.model.Budget;
import com.example.expense_tracker.model.Expense;
import com.example.expense_tracker.model.User;
import com.example.expense_tracker.repository.AnalyticsRepository;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private UserRepository userRepository;

    public AnalyticsResponse getAnalytics(String email, LocalDate startDate, LocalDate endDate) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateDateRange(startDate, endDate);

        SpendingSummaryDTO summary = getSpendingSummary(user.getId(), startDate, endDate);
        BigDecimal totalSpent = summary.totalSpent();

        List<SpendingTrendDTO> trends = getSpendingTrends(user.getId(), startDate, endDate);

        List<CategorySpendingDTO> categorySpending = getCategoryBreakdown(user.getId(), startDate, endDate, totalSpent);

        BudgetAnalysisDTO budgetAnalysis = getBudgetAnalysis(user.getId(), startDate, endDate, totalSpent);

        List<TopExpenseDTO> topExpenses = getTopExpenses(user.getId(), startDate, endDate);

        return new AnalyticsResponse(
                summary,
                trends,
                categorySpending,
                budgetAnalysis,
                topExpenses
        );
    }

    //spending summary
    private SpendingSummaryDTO getSpendingSummary(Integer userId, LocalDate startDate, LocalDate endDate) {
        //current
        List<Object[]> results = analyticsRepository.getSpendingSummary(userId, startDate, endDate);

        Object[] result = results.get(0);

        BigDecimal totalSpent = toBigDecimal(result[0]);
        long transactionCount = ((Number) result[1]).longValue();

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        BigDecimal dailyAverage = totalSpent.divide(
                BigDecimal.valueOf(days),
                2,
                RoundingMode.HALF_UP
        );


        //previous
        YearMonth currentMonth = YearMonth.from(startDate);
        LocalDate previousEnd = startDate;
        LocalDate previousStart = currentMonth.minusMonths(1).atDay(1);

        List<Object[]> previousResults = analyticsRepository.getSpendingSummary(userId, previousStart, previousEnd);

        Object[] previousResult = previousResults.get(0);

        BigDecimal totalPreviousSpent = toBigDecimal(previousResult[0]);


        //percent change
        BigDecimal percentChange;

        if (totalPreviousSpent.compareTo(BigDecimal.ZERO) == 0) {
            percentChange = BigDecimal.ZERO;
        } else {
            percentChange = totalSpent.subtract(totalPreviousSpent)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalPreviousSpent, 2, RoundingMode.HALF_UP);
        }


        return new SpendingSummaryDTO(
                totalSpent,
                dailyAverage,
                transactionCount,
                totalPreviousSpent,
                percentChange
        );
    }

    //helper
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(((Number) value).doubleValue())
                .setScale(2, RoundingMode.HALF_UP);
    }


    //trends
    private List<SpendingTrendDTO> getSpendingTrends(Integer userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = analyticsRepository.getSpendingTrends(userId, startDate, endDate);

        //create a map of date and amount
        Map<LocalDate, BigDecimal> spendingMap = new HashMap<>();
        for (Object[] row : results) {
            spendingMap.put(
                    (LocalDate) row[0],
                    toBigDecimal(row[1])
            );
        }

        //transform map into list of dto
        List<SpendingTrendDTO> trends = new ArrayList<>();

        for (LocalDate date = startDate;
             date.isBefore(endDate);
             date = date.plusDays(1)) {

            trends.add(new SpendingTrendDTO(
                    date,
                    spendingMap.getOrDefault(date, BigDecimal.ZERO)
            ));
        }

        return trends;
    }

    //category breakdown
    private List<CategorySpendingDTO> getCategoryBreakdown(Integer userId, LocalDate startDate, LocalDate endDate, BigDecimal totalSpent) {
        List<Object[]> results = analyticsRepository.getCategoryBreakdown(userId, startDate, endDate);

        List<CategorySpendingDTO> categories = new ArrayList<>();

        for (Object[] row : results) {
            String category = (String) row[0];
            BigDecimal amount = toBigDecimal(row[1]);

            BigDecimal percentage = BigDecimal.ZERO;

            if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
                percentage = amount.multiply(BigDecimal.valueOf(100))
                        .divide(totalSpent, 2, RoundingMode.HALF_UP);
            }

            categories.add(new CategorySpendingDTO(category, amount, percentage));
        }

        return categories;
    }

    //budget analysis
    private BudgetAnalysisDTO getBudgetAnalysis(Integer userId, LocalDate startDate, LocalDate endDate, BigDecimal totalSpent) {
        //check for if the start and end belong to a single month
        if (startDate.getYear() != endDate.minusDays(1).getYear()
                || startDate.getMonth() != endDate.minusDays(1).getMonth()) {
            return null;
        }

        Budget budget = budgetRepository.findByUserIdAndYearAndMonth(userId, startDate.getYear(), startDate.getMonthValue()
        ).orElse(null);

        if(budget == null) return null;

        BigDecimal budgetAmount = budget.getAmount();
        BigDecimal remainingAmount = budgetAmount.subtract(totalSpent);

        BigDecimal utilizationPercent = budgetAmount.compareTo(BigDecimal.ZERO) > 0
                ? totalSpent.multiply(BigDecimal.valueOf(100))
                    .divide(budgetAmount, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new BudgetAnalysisDTO(
                budgetAmount,
                totalSpent,
                remainingAmount,
                utilizationPercent
        );
    }

    //top expenses
    private List<TopExpenseDTO> getTopExpenses(Integer userId, LocalDate startDate, LocalDate endDate){
        List<Expense> expenses = analyticsRepository.getTopExpenses(
                userId,
                startDate,
                endDate,
                PageRequest.of(0,3)
        );

        return expenses.stream()
                .map(expense -> new TopExpenseDTO(
                        expense.getId(),
                        expense.getCategory(),
                        BigDecimal.valueOf(expense.getAmount()),
                        expense.getDescription(),
                        expense.getExpense_date()
                )).toList();
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (ChronoUnit.DAYS.between(startDate, endDate) > 365) {
            throw new IllegalArgumentException("Date range cannot exceed 366 days");
        }
    }
}
