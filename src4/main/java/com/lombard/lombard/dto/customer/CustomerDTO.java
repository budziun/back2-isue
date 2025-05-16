package com.lombard.lombard.dto.customer;

import com.lombard.lombard.models.Customer.IdType;
import java.time.LocalDate;

public class CustomerDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private IdType idType;
    private String idNumber;
    private LocalDate registrationDate;
    private boolean doNotServe;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isDoNotServe() {
        return doNotServe;
    }

    public void setDoNotServe(boolean doNotServe) {
        this.doNotServe = doNotServe;
    }
}
