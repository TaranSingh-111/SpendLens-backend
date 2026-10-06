package com.example.expense_tracker;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends CrudRepository<Expense, Integer> {

    public List<Expense> findByCategory(String category);

    @Query("SELECT SUM(e.amount) FROM Expense e")
    Float getTotal();

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category = :category")
    Float getCategoryTotal(@Param("category") String category);
}
