package com.example.expense_tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    //GET ALL
    @RequestMapping("/expenses")
    public List<Expense> getExpenses(){
        return expenseService.getAllExpenses();
    }

    //GET BY ID
    @RequestMapping("/expenses/{id}")
    public Expense getExpense(@PathVariable int id){
        return expenseService.getExpense(id);
    }

    //GET BY CATEGORY
    @RequestMapping("/expenses/category")
    public List<Expense> getExpenseByCategory(@RequestParam String category){
        return expenseService.getExpenseByCategory(category);
    }


    @RequestMapping("/expenses/total")
    public Float getTotalAmount(){
        return expenseService.getTotalAmount();
    }

    @RequestMapping("/expenses/total/category")
    public Float getTotalAmountByCategory(@RequestParam String category){
        return expenseService.getTotolAmountByCategory(category);
    }

    //Post
    @RequestMapping(method = RequestMethod.POST, value = "/expenses")
    public void addExpense(@RequestBody Expense expense){
        System.out.println("Reached Controller");
        expenseService.addExpense(expense);
    }

    //PUT
    @RequestMapping(method = RequestMethod.PUT, value = "/expenses/{id}")
    public void update(@PathVariable int id, @RequestBody Expense expense){
        expenseService.updateExpense(id, expense);
    }

    //DELETE
    @RequestMapping(method = RequestMethod.DELETE, value = "/expenses/{id}")
    public  void delete(@PathVariable int id){
        expenseService.deleteExpense(id);
    }
}
