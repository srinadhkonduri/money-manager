package com.moneymanager.service;

import com.moneymanager.dto.ExpenseDto;
import com.moneymanager.entity.CategoryEntity;
import com.moneymanager.entity.ExpenseEntity;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.CategoryRepository;
import com.moneymanager.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    // adding a new expense to DB
    public ExpenseDto addExpense(ExpenseDto expenseDto){
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findById(expenseDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        ExpenseEntity newExpense = toEntity(expenseDto, profileEntity, categoryEntity);
        newExpense = expenseRepository.save(newExpense);

        return toDto(newExpense);
    }

    // get expenses for the current month with start date and end date



    // helper methods
    private ExpenseEntity toEntity(ExpenseDto expenseDto, ProfileEntity profileEntity, CategoryEntity categoryEntity){
        return ExpenseEntity.builder()
                .name(expenseDto.getName())
                .icon(expenseDto.getIcon())
                .date(expenseDto.getDate())
                .amount(expenseDto.getAmount())
                .profileEntity(profileEntity)
                .categoryEntity(categoryEntity)
                .build();
    }

    private ExpenseDto toDto(ExpenseEntity expenseEntity){
        return ExpenseDto.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .icon(expenseEntity.getIcon())
                .categoryId(expenseEntity.getCategoryEntity() != null ? expenseEntity.getCategoryEntity().getId() : null)
                .categoryName(expenseEntity.getCategoryEntity() != null ? expenseEntity.getCategoryEntity().getName() : null)
                .amount(expenseEntity.getAmount())
                .date(expenseEntity.getDate())
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .build();
    }
}
