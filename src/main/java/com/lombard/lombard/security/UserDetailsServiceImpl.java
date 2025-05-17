package com.lombard.lombard.security;

import com.lombard.lombard.models.Employee;
import com.lombard.lombard.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public UserDetailsServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("employee not found with login " + username));

        return new User(
                employee.getLogin(),
                employee.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
    }
}
