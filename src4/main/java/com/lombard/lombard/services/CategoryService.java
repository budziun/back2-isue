package com.lombard.lombard.services;

import com.lombard.lombard.models.Category;
import com.lombard.lombard.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CategoryResponse createCategory(Category category) {
        try {
            if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
                return new CategoryResponse(null, "category exists");
            }

            // Obsługa relacji parent-child
            if (category.getParentCategory() != null && category.getParentCategory().getId() != null) {
                Optional<Category> parentCategoryOpt = categoryRepository.findById(category.getParentCategory().getId());
                if (parentCategoryOpt.isEmpty()) {
                    return new CategoryResponse(null, "parent category not found");
                }
                category.setParentCategory(parentCategoryOpt.get());
            }

            Category savedCategory = categoryRepository.save(category);
            return new CategoryResponse(savedCategory, null);
        } catch (Exception e) {
            return new CategoryResponse(null, e.getMessage());
        }
    }

    @Transactional
    public CategoryResponse createCategory(Category category, Integer specificId) {
        try {
            if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
                return new CategoryResponse(null, "category exists");
            }

            // Obsługa relacji parent-child
            if (category.getParentCategory() != null && category.getParentCategory().getId() != null) {
                Optional<Category> parentCategoryOpt = categoryRepository.findById(category.getParentCategory().getId());
                if (parentCategoryOpt.isEmpty()) {
                    return new CategoryResponse(null, "parent category not found");
                }
                category.setParentCategory(parentCategoryOpt.get());
            }

            // Jeśli podano konkretne ID, ustawiamy je
            if (specificId != null) {
                // Sprawdzamy czy ID jest już zajęte
                if (categoryRepository.existsById(specificId)) {
                    // Próbujmy usunąć istniejący rekord przed dodaniem nowego
                    try {
                        categoryRepository.deleteById(specificId);
                    } catch (Exception ex) {
                        return new CategoryResponse(null, "Cannot replace existing category: " + ex.getMessage());
                    }
                }
                category.setId(specificId);
            }

            Category savedCategory = categoryRepository.save(category);
            return new CategoryResponse(savedCategory, null);
        } catch (OptimisticLockingFailureException e) {
            return new CategoryResponse(null, "Conflict occurred: category with this ID was modified by another user");
        } catch (Exception e) {
            return new CategoryResponse(null, e.getMessage());
        }
    }

    @Transactional
    public CategoryResponse updateCategory(Integer id, Category categoryDetails) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            return new CategoryResponse(null, "category not found");
        }

        Category existingCategory = categoryOpt.get();

        try {
            // Sprawdzenie, czy nazwa kategorii jest unikalna (jeśli została zmieniona)
            if (categoryDetails.getCategoryName() != null &&
                !existingCategory.getCategoryName().equals(categoryDetails.getCategoryName()) &&
                categoryRepository.existsByCategoryName(categoryDetails.getCategoryName())) {
                return new CategoryResponse(null, "category exists");
            }

            // Aktualizacja podstawowych pól
            if (categoryDetails.getCategoryName() != null) {
                existingCategory.setCategoryName(categoryDetails.getCategoryName());
            }
            if (categoryDetails.getDescription() != null) {
                existingCategory.setDescription(categoryDetails.getDescription());
            }

            // Obsługa relacji parent-child
            if (categoryDetails.getParentCategory() != null) {
                Integer parentCategoryId = categoryDetails.getParentCategory().getId();

                // Sprawdzenie czy kategoria nie jest swoim własnym rodzicem
                if (parentCategoryId != null && parentCategoryId.equals(id)) {
                    return new CategoryResponse(null, "category cannot be its own parent");
                }

                Optional<Category> parentCategoryOpt = categoryRepository.findById(parentCategoryId);
                if (parentCategoryOpt.isEmpty()) {
                    return new CategoryResponse(null, "parent category not found");
                }

                // Sprawdzenie czy nie tworzymy cyklu w hierarchii kategorii
                Category parent = parentCategoryOpt.get();
                if (isChildOf(parent, existingCategory)) {
                    return new CategoryResponse(null, "cyclic hierarchy detected");
                }

                existingCategory.setParentCategory(parent);
            } else {
                existingCategory.setParentCategory(null);
            }

            Category updatedCategory = categoryRepository.save(existingCategory);
            return new CategoryResponse(updatedCategory, null);
        } catch (OptimisticLockingFailureException e) {
            return new CategoryResponse(null, "Conflict occurred: category was modified by another user");
        } catch (Exception e) {
            return new CategoryResponse(null, e.getMessage());
        }
    }

    // Metoda pomocnicza do sprawdzania czy istnieje cykl w hierarchii
    private boolean isChildOf(Category possibleChild, Category possibleParent) {
        if (possibleChild == null || possibleParent == null) {
            return false;
        }

        Category parent = possibleChild.getParentCategory();
        while (parent != null) {
            if (parent.getId().equals(possibleParent.getId())) {
                return true;
            }
            parent = parent.getParentCategory();
        }

        return false;
    }

    @Transactional
    public boolean deleteCategory(Integer id) {
        System.out.println("Trying to delete category with ID: " + id);
        return categoryRepository.findById(id)
                .map(category -> {
                    try {
                        // Sprawdź czy kategoria ma podkategorie
                        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
                            System.out.println("Category has " + category.getSubcategories().size() + " subcategories");
                            return false;
                        }

                        // Sprawdź czy kategoria ma powiązane przedmioty
                        if (category.getItems() != null && !category.getItems().isEmpty()) {
                            System.out.println("Category has " + category.getItems().size() + " items");
                            return false;
                        }

                        categoryRepository.delete(category);
                        System.out.println("Category deleted successfully");
                        return true;
                    } catch (Exception e) {
                        System.err.println("Error deleting category: " + e.getMessage());
                        e.printStackTrace();
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