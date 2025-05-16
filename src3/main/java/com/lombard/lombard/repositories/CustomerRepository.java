package com.lombard.lombard.repositories;

import com.lombard.lombard.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE c.firstName LIKE %?1% OR c.lastName LIKE %?1%")
    List<Customer> findByNameContaining(String name);

    List<Customer> findByDoNotServeTrue();
}