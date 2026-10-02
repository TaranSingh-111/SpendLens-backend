package com.example.expense_tracker;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExpenseRepository extends CrudRepository<Expense, Integer> {

    public List<Expense> findByCategory(String category);
}
