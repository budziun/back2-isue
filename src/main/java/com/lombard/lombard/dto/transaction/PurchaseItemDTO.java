package com.lombard.lombard.dto.transaction;

import java.math.BigDecimal;

public class PurchaseItemDTO {
    private Integer categoryId;
    private String name;
    private String description;
    private String serialNumber;
    private String brand;
    private String model;
    private String condition;
    private BigDecimal boughtFor;
    private BigDecimal askingPrice;

    // Getters and Setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public BigDecimal getBoughtFor() {
        return boughtFor;
    }

    public void setBoughtFor(BigDecimal boughtFor) {
        this.boughtFor = boughtFor;
    }

    public BigDecimal getAskingPrice() {
        return askingPrice;
    }

    public void setAskingPrice(BigDecimal askingPrice) {
        this.askingPrice = askingPrice;
    }
}
