package com.lombard.lombard.services;

import com.lombard.lombard.models.Category;
import com.lombard.lombard.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    public CategoryResponse createCategory(Category category) {
        try {
            if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
                return new CategoryResponse(null, "category exists");
            }

            Category savedCategory = categoryRepository.save(category);
            return new CategoryResponse(savedCategory, null);
        } catch (Exception e) {
            return new CategoryResponse(null, e.getMessage());
        }
    }

    public CategoryResponse updateCategory(Integer id, Category categoryDetails) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return new CategoryResponse(null, "category not found");
        }

        Category existingCategory = categoryOpt.get();

        try {
            if (!existingCategory.getCategoryName().equals(categoryDetails.getCategoryName()) &&
                    categoryRepository.existsByCategoryName(categoryDetails.getCategoryName())) {
                return new CategoryResponse(null, "category exists");
            }

            existingCategory.setCategoryName(categoryDetails.getCategoryName());
            existingCategory.setDescription(categoryDetails.getDescription());

            Category updatedCategory = categoryRepository.save(existingCategory);
            return new CategoryResponse(updatedCategory, null);
        } catch (Exception e) {
            return new CategoryResponse(null, e.getMessage());
        }
    }

    public boolean deleteCategory(Integer id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    try {
                        categoryRepository.delete(category);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .orElse(false);
    }

    public static class CategoryResponse {
        private final Category category;
        private final String errorMessage;

        public CategoryResponse(Category category, String errorMessage) {
            this.category = category;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return category != null;
        }

        public Category getCategory() {
            return category;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}