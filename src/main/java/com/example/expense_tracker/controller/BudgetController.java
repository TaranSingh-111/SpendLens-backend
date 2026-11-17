package com.example.expense_tracker.controller;

import com.example.expense_tracker.dto.BudgetRequest;
import com.example.expense_tracker.dto.BudgetResponse;
import com.example.expense_tracker.model.Budget;
import com.example.expense_tracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<BudgetResponse> getBudget(
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication authentication){

        Budget budget = budgetService.getBudget(authentication.getName(), year, month);
        return ResponseEntity.ok(toResponse(budget));
    }

    @PutMapping("/current")
    public ResponseEntity<BudgetResponse> saveCurrentBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication
    ) {
        LocalDate currentDate = LocalDate.now();

        Budget budget = budgetService.saveBudget(
                authentication.getName(),
                currentDate.getYear(),
                currentDate.getMonthValue(),
                request.amount()
        );

        return ResponseEntity.ok(toResponse(budget));
    }

    private BudgetResponse toResponse(Budget budget){
        return new BudgetResponse(
                budget.getId(),
                budget.getYear(),
                budget.getMonth(),
                budget.getAmount()
        );
    }
}
