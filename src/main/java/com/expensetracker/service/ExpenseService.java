package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  @Autowired
  public ExpenseService(ExpenseRepository expenseRepository) {
    this.expenseRepository = expenseRepository;
  }

  public List<Expense> getAllExpenses() {
    return expenseRepository.findAll();
  }

  public Expense saveExpense(Expense expense) {
    if (expense.getDate() == null) {
      expense.setDate(LocalDate.now());
    }
    return expenseRepository.save(expense);
  }

  public Expense getExpenseById(Long id) {
    return expenseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
  }

  public void deleteExpense(Long id) {
    expenseRepository.deleteById(id);
  }

  public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
    return expenseRepository.findByDateBetween(startDate, endDate);
  }

  public List<Expense> getExpensesByCategory(String category) {
    return expenseRepository.findByCategory(category);
  }
}