package com.moneymanager.service;


import com.moneymanager.dto.IncomeDto;
import com.moneymanager.entity.CategoryEntity;
import com.moneymanager.entity.IncomeEntity;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.CategoryRepository;
import com.moneymanager.repositories.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
