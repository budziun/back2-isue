package com.lombard.lombard.services;

import com.lombard.lombard.models.Customer;
import com.lombard.lombard.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }


    public Optional<Customer> getCustomerById(Integer id) {
        return customerRepository.findById(id);
    }


    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByNameContaining(name);
    }

    public List<Customer> getDoNotServeCustomers() {
        return customerRepository.findByDoNotServeTrue();
    }


    public Customer createCustomer(Customer customer) {
        if (customer.getRegistrationDate() == null) {
            customer.setRegistrationDate(LocalDate.now());
        }

        return customerRepository.save(customer);
    }

    public Optional<Customer> updateCustomer(Integer id, Customer customerDetails) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setFirstName(customerDetails.getFirstName());
                    existingCustomer.setLastName(customerDetails.getLastName());
                    existingCustomer.setIdType(customerDetails.getIdType());
                    existingCustomer.setIdNumber(customerDetails.getIdNumber());
                    existingCustomer.setDoNotServe(customerDetails.isDoNotServe());

                    return customerRepository.save(existingCustomer);
                });
    }

    public boolean deleteCustomer(Integer id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customerRepository.delete(customer);
                    return true;
                })
                .orElse(false);
    }

    public Optional<Customer> updateCustomerFlag(Integer id, boolean doNotServe) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setDoNotServe(doNotServe);
                    return customerRepository.save(customer);
                });
    }
}