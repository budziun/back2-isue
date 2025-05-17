package com.lombard.lombard.dto.transaction;

import java.time.LocalDate;
import java.util.List;

public class UpdateTransactionDTO {
    private LocalDate transactionDate;
    private List<TransactionItemDTO> items;
    private Integer customerId;

    public UpdateTransactionDTO() {
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public List<TransactionItemDTO> getItems() {
        return items;
    }

    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
