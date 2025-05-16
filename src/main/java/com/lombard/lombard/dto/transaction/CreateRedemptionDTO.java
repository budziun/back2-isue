package com.lombard.lombard.dto.transaction;

public class CreateRedemptionDTO {
    private Integer pawnTransactionId;
    private String notes;

    // Getters and Setters
    public Integer getPawnTransactionId() {
        return pawnTransactionId;
    }

    public void setPawnTransactionId(Integer pawnTransactionId) {
        this.pawnTransactionId = pawnTransactionId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}