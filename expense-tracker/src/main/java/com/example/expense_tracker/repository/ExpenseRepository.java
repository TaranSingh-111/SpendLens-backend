package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.Expense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ExpenseRepository extends CrudRepository<Expense, Integer> {

    public List<Expense> findByCategory(String category);

    public List<Expense> findByUserId(Integer userId);

    Optional<Expense> findByIdAndUserId(Integer id, Integer userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    Float getTotalByUserId(@Param("userId") Integer userId);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
            AND e.category = :category
            AND e.expense_date >= :startDate
            AND e.expense_date < :endDate
            """)
    Float getCategoryTotalByUserId(
            @Param("userId") Integer userId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT e.category, COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
            AND e.expense_date >= :startDate
            AND e.expense_date < :endDate
            GROUP BY e.category
            """)
    List<Object[]> getCategoryTotalsByUserId(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
            SELECT DISTINCT e.category
            FROM Expense e
            WHERE e.user.id = :userId
            ORDER BY e.category
            """)
    List<String> findDistinctCategoriesByUserId(
            @Param("userId") Integer userId
    );
}
