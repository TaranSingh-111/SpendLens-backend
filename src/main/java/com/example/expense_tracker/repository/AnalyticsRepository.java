package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsRepository extends JpaRepository<Expense, Integer> {

    //total amount and count
    @Query("""
            SELECT
                COALESCE (SUM(e.amount), 0.0),
                COUNT(e.id)
            FROM Expense e
            WHERE e.user.id = :userId
            AND e.expense_date >= :startDate
            AND e.expense_date < :endDate
            """)
    List<Object[]> getSpendingSummary(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    //chart data, has expenses by day
    @Query("""
            SELECT
                e.expense_date,
                SUM(e.amount)
            FROM Expense e
            WHERE e.user.id = :userId
            AND e.expense_date >= :startDate
            AND e.expense_date < :endDate
            GROUP BY e.expense_date
            ORDER BY e.expense_date
            """)
    List<Object[]> getSpendingTrends(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    //category and amount
    @Query("""
            SELECT
                e.category,
                SUM(e.amount)
            FROM Expense e
            WHERE e.user.id = :userId
            AND e.expense_date >= :startDate
            AND e.expense_date < :endDate
            GROUP BY e.category
            ORDER BY SUM(e.amount) DESC
            """)
    List<Object[]> getCategoryBreakdown(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    //top expenses
    @Query("""
            SELECT e
            FROM Expense e
            WHERE e.user.id = :userId
            AND e.expense_date >= :startDate
            AND e.expense_date < :endDate
            ORDER BY e.amount DESC
            """)
    List<Expense> getTopExpenses(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}
