package com.moneymanager.service;

import com.moneymanager.dto.ExpenseDto;
import com.moneymanager.entity.CategoryEntity;
import com.moneymanager.entity.ExpenseEntity;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.CategoryRepository;
import com.moneymanager.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


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
    public List<ExpenseDto> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity entity = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list = expenseRepository.findByProfileEntity_IdAndDateBetween(entity.getId(), startDate, endDate);
        return list.stream().map(this::toDto).toList();
    }


    // delete expense by id for current user
    public void deleteExpense(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("no expense id found"));
        if (!entity.getProfileEntity().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(entity);
    }

    // getting the latest top 5 expenses for the user
    public List<ExpenseDto> getTop5ExpenseForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> entity = expenseRepository.findTop5ByProfileEntity_IdOrderByDateDesc(profile.getId());
        return entity.stream().map(this::toDto).toList();
    }

    // get total expenses for current user
    public BigDecimal getTotalExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal expenses = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return expenses != null ? expenses : BigDecimal.ZERO;
    }

    // filter expenses
    public List<ExpenseDto> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> entity = expenseRepository.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return entity.stream().map(this::toDto).toList();
    }



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
