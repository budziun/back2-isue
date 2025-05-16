package com.lombard.lombard.dto.category;

public class UpdateCategoryDTO {
    private String categoryName;
    private String description;

    // Getters and Setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    private Integer parentCategoryId;
}