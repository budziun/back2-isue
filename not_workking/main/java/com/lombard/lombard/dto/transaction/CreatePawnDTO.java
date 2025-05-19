package com.lombard.lombard.dto.transaction;

import com.lombard.lombard.dto.customer.CreateCustomerDTO;

import java.math.BigDecimal;
import java.util.List;

public class CreatePawnDTO {
    private Integer customerId;
    private CreateCustomerDTO newCustomer; // For creating a new customer during pawn
    private Integer pawnDurationDays;
    private BigDecimal interestRate;
    private String notes;
    private List<PawnItemDTO> items;

    // Getters and Setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public CreateCustomerDTO getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(CreateCustomerDTO newCustomer) {
        this.newCustomer = newCustomer;
    }

    public Integer getPawnDurationDays() {
        return pawnDurationDays;
    }

    public void setPawnDurationDays(Integer pawnDurationDays) {
        this.pawnDurationDays = pawnDurationDays;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PawnItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PawnItemDTO> items) {
        this.items = items;
    }
}