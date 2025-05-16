package com.lombard.lombard.controllers;

import com.lombard.lombard.dto.category.CategoryDTO;
import com.lombard.lombard.dto.category.CreateCategoryDTO;
import com.lombard.lombard.dto.category.UpdateCategoryDTO;
import com.lombard.lombard.models.Category;
import com.lombard.lombard.services.CategoryService;
import com.lombard.lombard.services.CategoryService.CategoryResponse;
import com.lombard.lombard.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;
    private final Mapper mapper;

    @Autowired
    public CategoryController(CategoryService categoryService, Mapper mapper) {
        this.categoryService = categoryService;
        this.mapper = mapper;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = mapper.toCategoryDTOList(categories);
        return ResponseEntity.ok(categoryDTOs);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id)
                .map(category -> ResponseEntity.ok(mapper.toCategoryDTO(category)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/name/{categoryName}")
    public ResponseEntity<CategoryDTO> getCategoryByName(@PathVariable String categoryName) {
        return categoryService.getCategoryByName(categoryName)
                .map(category -> ResponseEntity.ok(mapper.toCategoryDTO(category)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryDTO categoryDTO) {
        Category category = mapper.toCategory(categoryDTO);
        CategoryResponse response = categoryService.createCategory(category);

        if (response.isSuccess()) {
            return new ResponseEntity<>(mapper.toCategoryDTO(response.getCategory()), HttpStatus.CREATED);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", response.getErrorMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody UpdateCategoryDTO categoryDTO) {
        return categoryService.getCategoryById(id)
                .map(existingCategory -> {
                    mapper.updateCategoryFromDTO(existingCategory, categoryDTO);

                    CategoryResponse response = categoryService.updateCategory(id, existingCategory);

                    if (response.isSuccess()) {
                        return ResponseEntity.ok(mapper.toCategoryDTO(response.getCategory()));
                    } else {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", response.getErrorMessage());
                        return ResponseEntity.badRequest().body(errorResponse);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteCategory(@PathVariable Integer id) {
        boolean deleted = categoryService.deleteCategory(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", deleted);

        return deleted ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
}