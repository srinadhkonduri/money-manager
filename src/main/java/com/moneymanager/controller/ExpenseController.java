package com.moneymanager.controller;

import com.moneymanager.dto.ExpenseDto;
import com.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/add")
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto expenseDto){
        ExpenseDto result = expenseService.addExpense(expenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getAllTheDetailsOfTheUserWithStartDateAndEndDate(){
        List<ExpenseDto> result = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
