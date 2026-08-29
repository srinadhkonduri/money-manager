package com.moneymanager.controller;

import com.moneymanager.dto.ExpenseDto;
import com.moneymanager.dto.FilterDto;
import com.moneymanager.dto.IncomeDto;
import com.moneymanager.service.ExpenseService;
import com.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1.0/filter")
@RequiredArgsConstructor
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDto filterDto){
        LocalDate startDate = filterDto.getStartDate() != null ? filterDto.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterDto.getEndDate() != null ? filterDto.getEndDate() : LocalDate.now();

        String keyword = filterDto.getKeyword() != null ? filterDto.getKeyword() : "";

        String sortField = filterDto.getSortField() != null ? filterDto.getSortField() : "date";

        Sort.Direction direction = "desc".equalsIgnoreCase(filterDto.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortField);
        if ("income".equalsIgnoreCase(filterDto.getType())){
            List<IncomeDto> incomes = incomeService.filterIncomes(startDate,endDate,keyword, sort);
            return ResponseEntity.ok(incomes);
        } else if ("expense".equalsIgnoreCase(filterDto.getType())) {
            List<ExpenseDto> expense = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expense);
        }
        else {
            return ResponseEntity.badRequest().body("Invalid must be expense ot income");
        }
    }
}
