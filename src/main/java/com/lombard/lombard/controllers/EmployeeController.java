package com.lombard.lombard.controllers;


import com.lombard.lombard.dto.employee.CreateEmployeeDTO;
import com.lombard.lombard.dto.employee.EmployeeDTO;
import com.lombard.lombard.dto.employee.EmployeeStatusDTO;
import com.lombard.lombard.dto.employee.UpdateEmployeeDTO;
import com.lombard.lombard.models.Employee;
import com.lombard.lombard.models.Employee.EmployeeStatus;
import com.lombard.lombard.models.Role;
import com.lombard.lombard.repositories.RoleRepository;
import com.lombard.lombard.services.EmployeeService;
import com.lombard.lombard.services.EmployeeService.EmployeeResponse;
import com.lombard.lombard.utils.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final RoleRepository roleRepository;
    private final Mapper mapper;

    @Autowired
    public EmployeeController(EmployeeService employeeService,
                              RoleRepository roleRepository,
                              Mapper mapper) {
        this.employeeService = employeeService;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        List<EmployeeDTO> employeeDTOs = mapper.toEmployeeDTOList(employees);
        return ResponseEntity.ok(employeeDTOs);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id)
                .map(employee -> ResponseEntity.ok(mapper.toEmployeeDTO(employee)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employees/search")
    public ResponseEntity<List<EmployeeDTO>> searchEmployeesByName(@RequestParam String name) {
        List<Employee> employees = employeeService.searchEmployeesByName(name);
        if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EmployeeDTO> employeeDTOs = mapper.toEmployeeDTOList(employees);
        return ResponseEntity.ok(employeeDTOs);
    }

    @GetMapping("/employees/status/{status}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByStatus(@PathVariable EmployeeStatus status) {
        List<Employee> employees = employeeService.getEmployeesByStatus(status);
        List<EmployeeDTO> employeeDTOs = mapper.toEmployeeDTOList(employees);
        return ResponseEntity.ok(employeeDTOs);
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeDTO employeeDTO) {
        try {
            Employee employee = mapper.toEmployee(employeeDTO);

            if (employeeDTO.getRoleId() != null) {
                Optional<Role> role = roleRepository.findById(employeeDTO.getRoleId());
                if (role.isPresent()) {
                    employee.setRole(role.get());
                } else {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Role not found");
                    return ResponseEntity.badRequest().body(errorResponse);
                }
            }

            EmployeeResponse response = employeeService.createEmployee(employee);

            if (response.isSuccess()) {
                return new ResponseEntity<>(mapper.toEmployeeDTO(response.getEmployee()), HttpStatus.CREATED);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", response.getErrorMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Integer id, @RequestBody UpdateEmployeeDTO employeeDTO) {
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);

        if (employeeOpt.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Employee not found");
            return ResponseEntity.notFound().build();
        }

        Employee employee = employeeOpt.get();

        mapper.updateEmployeeFromDTO(employee, employeeDTO);

        if (employeeDTO.getRoleId() != null) {
            Optional<Role> role = roleRepository.findById(employeeDTO.getRoleId());
            if (role.isPresent()) {
                employee.setRole(role.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Role not found");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

        EmployeeResponse response = employeeService.updateEmployee(id, employee);

        if (response.isSuccess()) {
            return ResponseEntity.ok(mapper.toEmployeeDTO(response.getEmployee()));
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", response.getErrorMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Integer id) {
        boolean deleted = employeeService.deleteEmployee(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", deleted);

        return deleted ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/employees/{id}/status")
    public ResponseEntity<EmployeeDTO> updateEmployeeStatus(@PathVariable Integer id,
                                                            @RequestBody EmployeeStatusDTO statusDTO) {
        return employeeService.updateEmployeeStatus(id, statusDTO.getStatus())
                .map(employee -> ResponseEntity.ok(mapper.toEmployeeDTO(employee)))
                .orElse(ResponseEntity.notFound().build());
    }
}