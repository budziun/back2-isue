package com.lombard.lombard.dto.employee;

import com.lombard.lombard.models.Employee.EmployeeStatus;

public class EmployeeStatusDTO {
    private EmployeeStatus status;

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }
}