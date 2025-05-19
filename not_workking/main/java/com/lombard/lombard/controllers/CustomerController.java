package com.lombard.lombard.controllers;


import com.lombard.lombard.dto.customer.CreateCustomerDTO;
import com.lombard.lombard.dto.customer.CustomerDTO;
import com.lombard.lombard.dto.customer.CustomerFlagDTO;
import com.lombard.lombard.dto.customer.UpdateCustomerDTO;
import com.lombard.lombard.models.Customer;
import com.lombard.lombard.services.CustomerService;
import com.lombard.lombard.utils.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;
    private final Mapper mapper;

    @Autowired
    public CustomerController(CustomerService customerService, Mapper mapper) {
        this.customerService = customerService;
        this.mapper = mapper;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        List<CustomerDTO> customerDTOs = mapper.toCustomerDTOList(customers);
        return ResponseEntity.ok(customerDTOs);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(mapper.toCustomerDTO(customer)))
                .orElse(ResponseEntity.<CustomerDTO>notFound().build());
    }

    @GetMapping("/customers/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomersByName(@RequestParam String name) {
        List<Customer> customers = customerService.searchCustomersByName(name);
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<CustomerDTO> customerDTOs = mapper.toCustomerDTOList(customers);
        return ResponseEntity.ok(customerDTOs);
    }

    @GetMapping("/customers/do-not-serve")
    public ResponseEntity<List<CustomerDTO>> getDoNotServeCustomers() {
        List<Customer> customers = customerService.getDoNotServeCustomers();
        List<CustomerDTO> customerDTOs = mapper.toCustomerDTOList(customers);
        return ResponseEntity.ok(customerDTOs);
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CreateCustomerDTO customerDTO) {
        try {
            Customer customer = mapper.toCustomer(customerDTO);
            Customer savedCustomer = customerService.createCustomer(customer);
            CustomerDTO savedCustomerDTO = mapper.toCustomerDTO(savedCustomer);
            return new ResponseEntity<>(savedCustomerDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.<CustomerDTO>badRequest().build();
        }
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteCustomer(@PathVariable Integer id) {
        boolean deleted = customerService.deleteCustomer(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", deleted);

        return deleted ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/customers/{id}/flag")
    public ResponseEntity<CustomerDTO> flagCustomer(@PathVariable Integer id,
                                                    @RequestBody CustomerFlagDTO flagDTO) {
        return customerService.updateCustomerFlag(id, flagDTO.isDoNotServe())
                .map(customer -> ResponseEntity.ok(mapper.toCustomerDTO(customer)))
                .orElse(ResponseEntity.<CustomerDTO>notFound().build());
    }
}