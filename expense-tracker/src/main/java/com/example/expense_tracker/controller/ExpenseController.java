package com.example.expense_tracker.controller;

import com.example.expense_tracker.model.Expense;
import com.example.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    //GET ALL
    @GetMapping("/expenses")
    public List<Expense> getExpenses(Authentication authentication){
        return expenseService.getAllExpenses(authentication.getName());
    }

    //GET BY ID
    @GetMapping("/expenses/{id}")
    public Expense getExpense(@PathVariable int id, Authentication authentication){
        return expenseService.getExpense(id, authentication.getName());
    }

    //GET CATEGORIES
    @GetMapping("/categories")
    public List<String> getCategories(Authentication authentication){
        return expenseService.getDistinctCategories(authentication.getName());
    }

    //GET BY CATEGORY
    @GetMapping("/expenses/category")
    public List<Expense> getExpenseByCategory(@RequestParam String category, Authentication authentication){
        return expenseService.getExpenseByCategory(category, authentication.getName());
    }


    @GetMapping("/expenses/total")
    public Float getTotalAmount(Authentication authentication){
        return expenseService.getTotalAmount(authentication.getName());
    }

    @GetMapping("/expenses/total/category")
    public Float getTotalAmountByCategory(@RequestParam String category, Authentication authentication){
        return expenseService.getTotalAmountByCategory(category, authentication.getName());
    }

    //Post
    @PostMapping("/expenses")
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody Expense expense, Authentication authentication){
        Expense savedExpense = expenseService.addExpense(expense, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    //PUT
    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> update(@PathVariable int id, @Valid @RequestBody Expense expense, Authentication authentication){
        Expense updatedExpense = expenseService.updateExpense(id, expense, authentication.getName());

        return ResponseEntity.status(HttpStatus.OK).body(updatedExpense);
    }

    //DELETE
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id, Authentication authentication){
        expenseService.deleteExpense(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
