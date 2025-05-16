package com.lombard.lombard.dto.transaction;

import com.lombard.lombard.dto.customer.CreateCustomerDTO;
import com.lombard.lombard.models.Transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// TransactionDTO.java - For returning transaction data
public class TransactionDTO {
    private Integer id;
    private Integer customerId;
    private String customerName;
    private Integer employeeId;
    private String employeeName;
    private LocalDate transactionDate;
    private TransactionType transactionType;
    private BigDecimal totalAmount;
    private Integer pawnDurationDays;
    private BigDecimal interestRate;
    private BigDecimal redemptionPrice;
    private LocalDate expiryDate;
    private Integer relatedTransactionId;
    private String notes;
    private List<TransactionItemDTO> items;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public BigDecimal getRedemptionPrice() {
        return redemptionPrice;
    }

    public void setRedemptionPrice(BigDecimal redemptionPrice) {
        this.redemptionPrice = redemptionPrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getRelatedTransactionId() {
        return relatedTransactionId;
    }

    public void setRelatedTransactionId(Integer relatedTransactionId) {
        this.relatedTransactionId = relatedTransactionId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TransactionItemDTO> getItems() {
        return items;
    }

    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }
}
