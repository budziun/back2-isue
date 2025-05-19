package com.lombard.lombard.controllers;


import com.lombard.lombard.dto.auth.LoginRequestDTO;
import com.lombard.lombard.dto.auth.LoginResponseDTO;
import com.lombard.lombard.models.Employee;
import com.lombard.lombard.repositories.EmployeeRepository;
import com.lombard.lombard.security.JwtTokenUtil;
import com.lombard.lombard.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final EmployeeRepository employeeRepository;
    private final Mapper mapper;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          UserDetailsService userDetailsService,
                          EmployeeRepository employeeRepository,
                          Mapper mapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequestDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || password == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Username and password are required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            Employee employee = employeeRepository.findByLogin(username)
                    .orElse(null);

            if (employee == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                return ResponseEntity.status(401).body(errorResponse);
            }

            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );
            } catch (DisabledException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "User is disabled");
                return ResponseEntity.status(401).body(errorResponse);
            } catch (BadCredentialsException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid password");
                return ResponseEntity.status(401).body(errorResponse);
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            final String token = jwtTokenUtil.generateToken(userDetails);

            LoginResponseDTO response = mapper.toLoginResponseDTO(token, employee);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication error: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}