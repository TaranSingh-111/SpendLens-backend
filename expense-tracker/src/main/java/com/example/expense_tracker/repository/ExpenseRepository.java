package com.example.expense_tracker.repository;

import com.example.expense_tracker.model.Expense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends CrudRepository<Expense, Integer> {

    public List<Expense> findByCategory(String category);

    public List<Expense> findByUserId(Integer userId);

    Optional<Expense> findByIdAndUserId(Integer id, Integer userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    Float getTotalByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(e.amount) " +
            "FROM Expense e " +
            "WHERE e.user.id = :userId AND e.category = :category")
    Float getCategoryTotalByUserId(
            @Param("userId") Integer userId,
            @Param("category") String category
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
