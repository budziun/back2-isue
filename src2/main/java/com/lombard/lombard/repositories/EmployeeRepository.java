package com.lombard.lombard.repositories;

import com.lombard.lombard.models.Employee;
import com.lombard.lombard.models.Employee.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("SELECT e FROM Employee e WHERE e.firstName LIKE %?1% OR e.lastName LIKE %?1%")
    List<Employee> findByNameContaining(String name);

    List<Employee> findByStatus(EmployeeStatus status);

    Optional<Employee> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}