package com.expensetracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public String listExpenses(Model model) {
        model.addAttribute("expenses", expenseService.getAllExpenses());
        model.addAttribute("expense", new Expense());
        return "expenses/list";
    }

    @PostMapping
    public String saveExpense(@Valid @ModelAttribute("expense") Expense expense,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("expenses", expenseService.getAllExpenses());
            return "expenses/list";
        }
        expenseService.saveExpense(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model) {
        model.addAttribute("expense", expenseService.getExpenseById(id));
        return "expenses/edit";
    }

    @PostMapping("/update/{id}")
    public String updateExpense(@PathVariable Long id,
                              @Valid @ModelAttribute("expense") Expense expense,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "expenses/edit";
        }
        expense.setId(id);
        expenseService.saveExpense(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return "redirect:/expenses";
    }
} 