package com.moneymanager.controller;

import com.moneymanager.dto.CategoryDto;
import com.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1.0/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // saving the category
    @PostMapping("/save")
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto){
        CategoryDto savedCategory = categoryService.saveCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    // getting all the categories for a particular profile id
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(){
        List<CategoryDto> result = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.ok(result);
    }

    // getting categories for current user by type
    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByTypeForCurrentUser(@PathVariable String type){
        List<CategoryDto> list =  categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(list);
    }

    // updating the category name and icon
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategoryNameAndIcon(@PathVariable Long id, @RequestBody CategoryDto categoryDto){
        CategoryDto result = categoryService.updateCategory(id,categoryDto);
        return ResponseEntity.ok(result);
    }
}
