package com.lombard.lombard.dto.transaction;

import com.lombard.lombard.dto.customer.CreateCustomerDTO;

import java.util.List;

public class CreatePurchaseDTO {
    private Integer customerId;
    private CreateCustomerDTO newCustomer; // For creating a new customer during purchase
    private String notes;
    private List<PurchaseItemDTO> items;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PurchaseItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItemDTO> items) {
        this.items = items;
    }
}
