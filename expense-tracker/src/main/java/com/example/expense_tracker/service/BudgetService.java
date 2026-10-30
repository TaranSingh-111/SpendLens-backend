package com.example.expense_tracker.service;

import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.model.Budget;
import com.example.expense_tracker.model.User;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Budget getBudget(String email, Integer year, Integer month){
        validateYearAndMonth(year, month);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return budgetRepository.findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
    }

    @Transactional
    public Budget saveBudget(String email, Integer year, Integer month, BigDecimal amount){
        validateYearAndMonth(year, month);

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Budget amount must be greater than zero");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Budget budget = budgetRepository
                .findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElseGet(() -> new Budget(null, user, year, month, amount));

        budget.setAmount(amount);

        return budgetRepository.save(budget);
    }


    private void validateYearAndMonth(Integer year, Integer month){
        if(year == null || year < 2000 || year > 9999){
            throw new IllegalArgumentException("Invalid year exception");
        }

        if(month == null || month < 1 || month > 12){
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }
    }
}
