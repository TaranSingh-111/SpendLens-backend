package com.example.expense_tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> getAllExpenses(){
        ArrayList<Expense> expenses = new ArrayList<>();
        expenseRepository.findAll().forEach(expenses::add);
        return expenses;
    }

    public Expense getExpense(int id){
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense Not Found"));
    }

    public List<Expense> getExpenseByCategory(String category){
        ArrayList<Expense> expenses = new ArrayList<>();
        expenseRepository.findByCategory(category).forEach(expenses::add);
        return expenses;
    }

    public Float getTotalAmount(){
        return expenseRepository.getTotal();
    }

    public Float getTotolAmountByCategory(String category){
        return expenseRepository.getCategoryTotal(category);
    }

    public void addExpense(Expense expense){
        expenseRepository.save(expense);
    }

    public void updateExpense(int id, Expense expense){
        expense.setId(id);
        expenseRepository.save(expense);
    }

    public void deleteExpense(int id){
        expenseRepository.deleteById(id);
    }
}
