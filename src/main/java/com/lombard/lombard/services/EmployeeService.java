package com.lombard.lombard.services;

import com.lombard.lombard.models.Employee;
import com.lombard.lombard.models.Employee.EmployeeStatus;
import com.lombard.lombard.models.Role;
import com.lombard.lombard.repositories.EmployeeRepository;
import com.lombard.lombard.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, RoleRepository roleRepository) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
    }


    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }


    public Optional<Employee> getEmployeeById(Integer id) {
        return employeeRepository.findById(id);
    }


    public List<Employee> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContaining(name);
    }


    public List<Employee> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status);
    }


    public EmployeeResponse createEmployee(Employee employee) {
        try {
            if (employeeRepository.existsByLogin(employee.getLogin())) {
                return new EmployeeResponse(null, "login exists");
            }

            if (employee.getEmail() != null && !employee.getEmail().isEmpty() &&
                    employeeRepository.existsByEmail(employee.getEmail())) {
                return new EmployeeResponse(null, "email exists");
            }

            if (employee.getRole() == null || employee.getRole().getId() == null) {
                return new EmployeeResponse(null, "role is required");
            }

            Optional<Role> role = roleRepository.findById(employee.getRole().getId());
            if (role.isEmpty()) {
                return new EmployeeResponse(null, "role not found");
            }

            employee.setRole(role.get());
            if (employee.getHireDate() == null) {
                employee.setHireDate(LocalDate.now());
            }

            if (employee.getStatus() == null) {
                employee.setStatus(EmployeeStatus.active);
            }

            Employee savedEmployee = employeeRepository.save(employee);
            return new EmployeeResponse(savedEmployee, null);

        } catch (Exception e) {
            return new EmployeeResponse(null, e.getMessage());
        }
    }


    public EmployeeResponse updateEmployee(Integer id, Employee employeeDetails) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);

        if (employeeOpt.isEmpty()) {
            return new EmployeeResponse(null, "employee not found");
        }

        Employee existingEmployee = employeeOpt.get();

        try {
            if (!existingEmployee.getLogin().equals(employeeDetails.getLogin()) &&
                    employeeRepository.existsByLogin(employeeDetails.getLogin())) {
                return new EmployeeResponse(null, "login already exists");
            }

            if (employeeDetails.getEmail() != null && !employeeDetails.getEmail().isEmpty() &&
                    !employeeDetails.getEmail().equals(existingEmployee.getEmail()) &&
                    employeeRepository.existsByEmail(employeeDetails.getEmail())) {
                return new EmployeeResponse(null, "email already exists");
            }

            if (employeeDetails.getRole() != null && employeeDetails.getRole().getId() != null) {
                Optional<Role> role = roleRepository.findById(employeeDetails.getRole().getId());
                if (role.isEmpty()) {
                    return new EmployeeResponse(null, "role not found");
                }
                existingEmployee.setRole(role.get());
            }

            // Update fields
            existingEmployee.setLogin(employeeDetails.getLogin());
            if (employeeDetails.getPasswordHash() != null && !employeeDetails.getPasswordHash().isEmpty()) {
                existingEmployee.setPasswordHash(employeeDetails.getPasswordHash());
            }
            existingEmployee.setFirstName(employeeDetails.getFirstName());
            existingEmployee.setLastName(employeeDetails.getLastName());
            existingEmployee.setHireDate(employeeDetails.getHireDate());
            existingEmployee.setAddress(employeeDetails.getAddress());
            existingEmployee.setPhoneNumber(employeeDetails.getPhoneNumber());
            existingEmployee.setEmail(employeeDetails.getEmail());
            existingEmployee.setStatus(employeeDetails.getStatus());

            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            return new EmployeeResponse(updatedEmployee, null);

        } catch (Exception e) {
            return new EmployeeResponse(null, e.getMessage());
        }
    }

    public boolean deleteEmployee(Integer id) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employeeRepository.delete(employee);
                    return true;
                })
                .orElse(false);
    }

    public Optional<Employee> updateEmployeeStatus(Integer id, EmployeeStatus status) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setStatus(status);
                    return employeeRepository.save(employee);
                });
    }
    
    public static class EmployeeResponse {
        private final Employee employee;
        private final String errorMessage;

        public EmployeeResponse(Employee employee, String errorMessage) {
            this.employee = employee;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return employee != null;
        }

        public Employee getEmployee() {
            return employee;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}