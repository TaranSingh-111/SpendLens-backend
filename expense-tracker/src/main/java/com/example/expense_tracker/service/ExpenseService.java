package com.example.expense_tracker.service;

import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.model.Expense;
import com.example.expense_tracker.model.User;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;

    //helpers
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Expense getExpenseByIdAndUser(int id, Integer userId){
        return expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense Not Found"));
    }


    //fetch
    public List<Expense> getAllExpenses(String email){
        User user = getUserByEmail(email);

        return expenseRepository.findByUserId(user.getId());
    }

    public Expense getExpense(int id, String email){
        User user = getUserByEmail(email);

        return getExpenseByIdAndUser(id, user.getId());
    }

    public List<String> getDistinctCategories(String email){
        User user = getUserByEmail(email);

        return expenseRepository.findDistinctCategoriesByUserId(user.getId());
    }

    public List<Expense> getExpenseByCategory(String category, String email){
        User user = getUserByEmail(email);

        return expenseRepository.findByUserId(user.getId())
                .stream()
                .filter(expense -> category.equals(expense.getCategory()))
                .toList();
    }

    public Float getTotalAmount(String email){
        User user = getUserByEmail(email);
        Float total = expenseRepository.getTotalByUserId(user.getId());

        return total != null ? total : 0.0f;
    }

    public Float getTotalAmountByCategory(String email, String category, LocalDate startDate, LocalDate endDate){
        User user = getUserByEmail(email);
        Float total = expenseRepository.getCategoryTotalByUserId(user.getId(), category, startDate, endDate);

        return total != null ? total : 0.0f;
    }

    public Map<String, Double> getCategoryTotals(String email, LocalDate startDate, LocalDate endDate) {
        User user = getUserByEmail(email);
        List<Object[]> results = expenseRepository.getCategoryTotalsByUserId(
                user.getId(),
                startDate,
                endDate
        );

        Map<String, Double> totals = new HashMap<>();

        for(Object[] result : results){
            String category = (String) result[0];
            Double amount = ((Number) result[1]).doubleValue();

            totals.put(category, amount);
        }
        return totals;
    }


    //operations
    public Expense addExpense(Expense expense, String email){
        User user = getUserByEmail(email);
        expense.setId(null);
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(int id, Expense expense, String email){
        User user = getUserByEmail(email);
        Expense existingExpense = getExpenseByIdAndUser(id, user.getId());

        existingExpense.setExpense_date(expense.getExpense_date());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setDescription(expense.getDescription());

        return expenseRepository.save(existingExpense);
    }

    public void deleteExpense(int id, String email){
        User user = getUserByEmail(email);
        Expense expense = getExpenseByIdAndUser(id, user.getId());
        expenseRepository.delete(expense);
    }
}
