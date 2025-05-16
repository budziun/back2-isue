package com.lombard.lombard.dto.customer;

import com.lombard.lombard.models.Customer.IdType;

public class UpdateCustomerDTO {
    private String firstName;
    private String lastName;
    private IdType idType;
    private String idNumber;
    private boolean doNotServe;

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public boolean isDoNotServe() {
        return doNotServe;
    }

    public void setDoNotServe(boolean doNotServe) {
        this.doNotServe = doNotServe;
    }
}
