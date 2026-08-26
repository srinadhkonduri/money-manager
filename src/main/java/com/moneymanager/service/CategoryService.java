package com.moneymanager.service;

import com.moneymanager.dto.CategoryDto;
import com.moneymanager.entity.CategoryEntity;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // save category
    public CategoryDto saveCategory(CategoryDto categoryDto){
        // first get the profile id with the help of profile entity
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDto.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with is name is alreay exist");
        }

        // first change it to entity and then save them to DB
        CategoryEntity newCategory = toEntity(categoryDto,profile);
        newCategory = categoryRepository.save(newCategory);

        // returning again to DTO
        return toDto(newCategory);
    }

    // get categories from current user
    public List<CategoryDto> getCategoriesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByProfileId(profile.getId());
        return categoryEntities.stream().map(this::toDto).toList();
    }

    // get categories for current user
    public List<CategoryDto> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> entities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return entities.stream().map(this::toDto).toList();
    }

    // updating the category
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(id, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found "));

        categoryEntity.setName(categoryDto.getName());
        categoryEntity.setType(categoryDto.getType());
        categoryEntity = categoryRepository.save(categoryEntity);

        return toDto(categoryEntity);
    }


    // helper methods
    private CategoryEntity toEntity(CategoryDto categoryDto, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .profile(profile)
                .type(categoryDto.getType())
                .build();
    }

    private CategoryDto toDto(CategoryEntity categoryEntity){
        return CategoryDto.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId() : null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }
}
