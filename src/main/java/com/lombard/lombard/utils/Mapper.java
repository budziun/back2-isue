package com.lombard.lombard.utils;

import com.lombard.lombard.dto.auth.LoginResponseDTO;
import com.lombard.lombard.dto.category.CategoryDTO;
import com.lombard.lombard.dto.category.CreateCategoryDTO;
import com.lombard.lombard.dto.category.UpdateCategoryDTO;
import com.lombard.lombard.dto.customer.CreateCustomerDTO;
import com.lombard.lombard.dto.customer.CustomerDTO;
import com.lombard.lombard.dto.customer.UpdateCustomerDTO;
import com.lombard.lombard.dto.employee.CreateEmployeeDTO;
import com.lombard.lombard.dto.employee.EmployeeDTO;
import com.lombard.lombard.dto.employee.UpdateEmployeeDTO;
import com.lombard.lombard.dto.role.CreateRoleDTO;
import com.lombard.lombard.dto.role.RoleDTO;
import com.lombard.lombard.dto.role.UpdateRoleDTO;
import com.lombard.lombard.dto.transaction.TransactionDTO;
import com.lombard.lombard.dto.transaction.TransactionItemDTO;
import com.lombard.lombard.models.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public Mapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerDTO toCustomerDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setIdType(customer.getIdType());
        dto.setIdNumber(customer.getIdNumber());
        dto.setRegistrationDate(customer.getRegistrationDate());
        dto.setDoNotServe(customer.isDoNotServe());
        
        return dto;
    }

    public List<CustomerDTO> toCustomerDTOList(List<Customer> customers) {
        return customers.stream()
                .map(this::toCustomerDTO)
                .collect(Collectors.toList());
    }

    public Customer toCustomer(CreateCustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setIdType(dto.getIdType());
        customer.setIdNumber(dto.getIdNumber());
        customer.setDoNotServe(dto.isDoNotServe());
        return customer;
    }


    public EmployeeDTO toEmployeeDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setLogin(employee.getLogin());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setRole(toRoleDTO(employee.getRole()));
        dto.setHireDate(employee.getHireDate());
        dto.setAddress(employee.getAddress());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setEmail(employee.getEmail());
        dto.setStatus(employee.getStatus());
        
        return dto;
    }

    public List<EmployeeDTO> toEmployeeDTOList(List<Employee> employees) {
        return employees.stream()
                .map(this::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    public Employee toEmployee(CreateEmployeeDTO dto) {
        if (dto == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setLogin(dto.getLogin());
        employee.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setHireDate(dto.getHireDate());
        employee.setAddress(dto.getAddress());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setEmail(dto.getEmail());
        employee.setStatus(dto.getStatus());

        return employee;
    }

    public void updateEmployeeFromDTO(Employee employee, UpdateEmployeeDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getLogin() != null) {
            employee.setLogin(dto.getLogin());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            employee.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getFirstName() != null) {
            employee.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            employee.setLastName(dto.getLastName());
        }

        if (dto.getHireDate() != null) {
            employee.setHireDate(dto.getHireDate());
        }

        if (dto.getAddress() != null) {
            employee.setAddress(dto.getAddress());
        }

        if (dto.getPhoneNumber() != null) {
            employee.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getEmail() != null) {
            employee.setEmail(dto.getEmail());
        }

        if (dto.getStatus() != null) {
            employee.setStatus(dto.getStatus());
        }

    }

    public RoleDTO toRoleDTO(Role role) {
        if (role == null) {
            return null;
        }

        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        dto.setPermissionsLevel(role.getPermissionsLevel());
        dto.setMaxBuy(role.getMaxBuy());
        
        return dto;
    }

    public List<RoleDTO> toRoleDTOList(List<Role> roles) {
        return roles.stream()
                .map(this::toRoleDTO)
                .collect(Collectors.toList());
    }

    public Role toRole(CreateRoleDTO dto) {
        if (dto == null) {
            return null;
        }

        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        role.setPermissionsLevel(dto.getPermissionsLevel());
        role.setMaxBuy(dto.getMaxBuy());
        return role;
    }

    public void updateRoleFromDTO(Role role, UpdateRoleDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getRoleName() != null) {
            role.setRoleName(dto.getRoleName());
        }

        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }

        if (dto.getPermissionsLevel() != null) {
            role.setPermissionsLevel(dto.getPermissionsLevel());
        }

        if (dto.getMaxBuy() != null) {
            role.setMaxBuy(dto.getMaxBuy());
        }
    }

    public LoginResponseDTO toLoginResponseDTO(String token, Employee employee) {
        if (employee == null) {
            return null;
        }

        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setToken(token);
        dto.setUsername(employee.getLogin());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setRole(employee.getRole().getRoleName());
        
        return dto;
    }

    public TransactionDTO toTransactionDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());

        if (transaction.getCustomer() != null) {
            dto.setCustomerId(transaction.getCustomer().getId());
            dto.setCustomerName(transaction.getCustomer().getFirstName() + " " + transaction.getCustomer().getLastName());
        }

        dto.setEmployeeId(transaction.getEmployee().getId());
        dto.setEmployeeName(transaction.getEmployee().getFirstName() + " " + transaction.getEmployee().getLastName());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setTotalAmount(transaction.getTotalAmount());
        dto.setPawnDurationDays(transaction.getPawnDurationDays());
        dto.setInterestRate(transaction.getInterestRate());
        dto.setRedemptionPrice(transaction.getRedemptionPrice());
        dto.setExpiryDate(transaction.getExpiryDate());

        if (transaction.getRelatedTransaction() != null) {
            dto.setRelatedTransactionId(transaction.getRelatedTransaction().getId());
        }

        dto.setNotes(transaction.getNotes());

        List<TransactionItem> transactionItems = transaction.getTransactionItems();
        if (transactionItems != null && !transactionItems.isEmpty()) {
            List<TransactionItemDTO> itemDTOs = transactionItems.stream()
                    .map(this::toTransactionItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        
        return dto;
    }

    public List<TransactionDTO> toTransactionDTOList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

   public TransactionItemDTO toTransactionItemDTO(TransactionItem transactionItem) {
       if (transactionItem == null) {
           return null;
       }

       TransactionItemDTO dto = new TransactionItemDTO();
       dto.setId(transactionItem.getId());

       Item item = transactionItem.getItem();
       if (item != null) {
           dto.setItemId(item.getId());
           dto.setItemName(item.getName());
           dto.setDescription(item.getDescription());
           dto.setBrand(item.getBrand());
           dto.setModel(item.getModel());
           dto.setSerialNumber(item.getSerialNumber());
           dto.setCondition(item.getCondition());

           if (item.getCategory() != null) {
               dto.setCategoryId(item.getCategory().getId());
           } else {
               dto.setCategoryId(null);
           }

           dto.setAskingPrice(item.getAskingPrice());
       }

       dto.setPrice(transactionItem.getPrice());

       
        return dto;
   }

    public CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());

        // Dodane mapowanie parentCategoryId
        if (category.getParentCategory() != null) {
            dto.setParentCategoryId(category.getParentCategory().getId());
        }

        return dto;
    }

    public Category toCategory(CreateCategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        // Jeśli id zostało podane, ustawiamy je
        if (dto.getId() != null) {
            category.setId(dto.getId());
        }
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());

        return category;
    }

    public void updateCategoryFromDTO(Category category, UpdateCategoryDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getCategoryName() != null) {
            category.setCategoryName(dto.getCategoryName());
        }

        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }

        // Obsługa parentCategoryId - odbywa się w CategoryService
        // dzięki czemu poprawność hierarchii jest sprawdzana w jednym miejscu
    }

    public List<CategoryDTO> toCategoryDTOList(List<Category> categories) {
        return categories.stream()
                .map(this::toCategoryDTO)
                .collect(Collectors.toList());
    }
}