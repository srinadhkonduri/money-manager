package com.moneymanager.service;

import com.moneymanager.dto.ExpenseDto;
import com.moneymanager.dto.IncomeDto;
import com.moneymanager.dto.RecentTranscationDto;
import com.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;



    public Map<String, Object> getDashBoardData(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<IncomeDto> latestIncomes = incomeService.getTop5IncomesForCurrentUser();
        List<ExpenseDto> latestExpenses = expenseService.getTop5ExpenseForCurrentUser();
        List<RecentTranscationDto> recentTransactionDtos = concat(latestIncomes.stream().map(income ->
                        RecentTranscationDto.builder()
                                .id(income.getId())
                                .profileId(profile.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("income")
                                .build()),
                        latestExpenses.stream().map(expense ->
                                RecentTranscationDto.builder()
                                        .id(expense.getId())
                                        .profileId(profile.getId())
                                        .name(expense.getName())
                                        .amount(expense.getAmount())
                                        .date(expense.getDate())
                                        .createdAt(expense.getCreatedAt())
                                        .updatedAt(expense.getUpdatedAt())
                                        .type("expense")
                                        .build()))
                .sorted((a, b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).collect(Collectors.toList());

        returnValue.put("totalBalance", incomeService.getTotalIncomesForCurrentUser()
                .subtract(expenseService.getTotalExpensesForCurrentUser()));

        returnValue.put("total incomes", incomeService.getTotalIncomesForCurrentUser());
        returnValue.put("total expenses", expenseService.getTotalExpensesForCurrentUser());
        returnValue.put("recent 5 expenses", latestExpenses);
        returnValue.put("recent 5 incomes", latestIncomes);
        returnValue.put("recent transactions", recentTransactionDtos);
        return returnValue;
    }
}
