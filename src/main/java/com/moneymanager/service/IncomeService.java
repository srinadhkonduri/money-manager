package com.moneymanager.service;


import com.moneymanager.dto.ExpenseDto;
import com.moneymanager.dto.IncomeDto;
import com.moneymanager.entity.CategoryEntity;
import com.moneymanager.entity.ExpenseEntity;
import com.moneymanager.entity.IncomeEntity;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.CategoryRepository;
import com.moneymanager.repositories.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    // adding a new income to DB
    public IncomeDto addIncome(IncomeDto incomeDto){
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findById(incomeDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newIncome = toEntity(incomeDto,profileEntity,categoryEntity);
        newIncome = incomeRepository.save(newIncome);

        return toDto(newIncome);
    }

    // getting all the incomes with the help specific start date and end dates
    public List<IncomeDto> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity entity = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileEntity_IdAndDateBetween(entity.getId(), startDate, endDate);
        return list.stream().map(this::toDto).toList();
    }

    // delete expense by id for current user
    public void deleteIncome(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("no income id found"));
        if (!entity.getProfileEntity().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(entity);
    }

    // getting the latest top 5 incomes for the user
    public List<IncomeDto> getTop5IncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> entity = incomeRepository.findTop5ByProfileEntity_IdOrderByDateDesc(profile.getId());
        return entity.stream().map(this::toDto).toList();
    }

    // get total incomes for current user
    public BigDecimal getTotalIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal incomes = incomeRepository.findTotalExpenseByProfileId(profile.getId());
        return incomes != null ? incomes : BigDecimal.ZERO;
    }

    // filter expenses
    public List<IncomeDto> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> entity = incomeRepository.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),
                startDate, endDate, keyword, sort);
        return entity.stream().map(this::toDto).toList();
    }

    // helper methods
    private IncomeEntity toEntity(IncomeDto incomeDto, ProfileEntity profileEntity, CategoryEntity categoryEntity){
        return IncomeEntity.builder()
                .name(incomeDto.getName())
                .icon(incomeDto.getIcon())
                .date(incomeDto.getDate())
                .amount(incomeDto.getAmount())
                .profileEntity(profileEntity)
                .categoryEntity(categoryEntity)
                .build();
    }

    private IncomeDto toDto(IncomeEntity incomeEntity){
        return IncomeDto.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .categoryId(incomeEntity.getCategoryEntity() != null ? incomeEntity.getCategoryEntity().getId() : null)
                .categoryName(incomeEntity.getCategoryEntity() != null ? incomeEntity.getCategoryEntity().getName() : null)
                .amount(incomeEntity.getAmount())
                .date(incomeEntity.getDate())
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }
}
