package com.lombard.lombard.controllers;

import com.lombard.lombard.dto.transaction.*;
import com.lombard.lombard.models.Customer;
import com.lombard.lombard.models.Employee;
import com.lombard.lombard.models.Transaction;
import com.lombard.lombard.models.Transaction.TransactionType;
import com.lombard.lombard.repositories.EmployeeRepository;
import com.lombard.lombard.repositories.TransactionRepository;
import com.lombard.lombard.security.JwtTokenUtil;
import com.lombard.lombard.services.CustomerService;
import com.lombard.lombard.services.TransactionService;
import com.lombard.lombard.services.TransactionService.TransactionResponse;
import com.lombard.lombard.utils.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;
    private final CustomerService customerService;
    private final EmployeeRepository employeeRepository;
    private final Mapper mapper;
    private final TransactionRepository transactionRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public TransactionController(
            TransactionService transactionService,
            CustomerService customerService,
            EmployeeRepository employeeRepository,
            JwtTokenUtil jwtTokenUtil,
            TransactionRepository transactionRepository,
            Mapper mapper) {
        this.transactionService = transactionService;
        this.customerService = customerService;
        this.employeeRepository = employeeRepository;
        this.transactionRepository = transactionRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.mapper = mapper;
    }

    /**
     * Get the currently authenticated employee
     * @return The current employee or null if not authenticated
     */
    private Employee getCurrentEmployee() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return employeeRepository.findByLogin(userDetails.getUsername()).orElse(null);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // get Transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionDTO> transactionDTOs = mapper.toTransactionDTOList(transactions);
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Integer id) {
        return transactionService.getTransactionById(id)
                .map(transaction -> ResponseEntity.ok(mapper.toTransactionDTO(transaction)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transactions/customer/{customerId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCustomer(@PathVariable Integer customerId) {
        Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Transaction> transactions = transactionService.getTransactionsByCustomer(customerOpt.get());
        List<TransactionDTO> transactionDTOs = mapper.toTransactionDTOList(transactions);
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/transactions/type/{type}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(@PathVariable TransactionType type) {
        List<Transaction> transactions = transactionService.getTransactionsByType(type);
        List<TransactionDTO> transactionDTOs = mapper.toTransactionDTOList(transactions);
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/transactions/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        List<TransactionDTO> transactionDTOs = mapper.toTransactionDTOList(transactions);
        return ResponseEntity.ok(transactionDTOs);
    }

    @GetMapping("/transactions/pawns/active/{customerId}")
    public ResponseEntity<List<TransactionDTO>> getActivePawnsByCustomer(@PathVariable Integer customerId) {
        List<Transaction> pawns = transactionService.getActivePawnsByCustomerId(customerId);
        List<TransactionDTO> pawnDTOs = mapper.toTransactionDTOList(pawns);
        return ResponseEntity.ok(pawnDTOs);
    }

    // purchase transaction
    @PostMapping("/transactions/purchase")
    public ResponseEntity<?> createPurchaseTransaction(@RequestBody CreatePurchaseDTO purchaseDTO) {
        Employee employee = getCurrentEmployee();
        if (employee == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        TransactionResponse response = transactionService.createPurchaseTransaction(purchaseDTO, employee);

        return handleTransactionResponse(response);
    }

    // sale transaction
    @PostMapping("/transactions/sale")
    public ResponseEntity<?> createSaleTransaction(@RequestBody CreateSaleDTO saleDTO) {
        Employee employee = getCurrentEmployee();
        if (employee == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        TransactionResponse response = transactionService.createSaleTransaction(saleDTO, employee);

        return handleTransactionResponse(response);
    }

    // delete transaction id
    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteTransaction(@PathVariable Integer id) {
        boolean deleted = transactionService.deleteTransaction(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", deleted);

        return deleted ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    // pawn transaction
    @PostMapping("/transactions/pawn")
    public ResponseEntity<?> createPawnTransaction(@RequestBody CreatePawnDTO pawnDTO) {
        Employee employee = getCurrentEmployee();
        if (employee == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        TransactionResponse response = transactionService.createPawnTransaction(pawnDTO, employee);

        return handleTransactionResponse(response);
    }

    // redemption transaction
    @PostMapping("/transactions/redemption")
    public ResponseEntity<?> createRedemptionTransaction(@RequestBody CreateRedemptionDTO redemptionDTO) {
        Employee employee = getCurrentEmployee();
        if (employee == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        TransactionResponse response = transactionService.createRedemptionTransaction(redemptionDTO, employee);

        return handleTransactionResponse(response);
    }

    @DeleteMapping("/transactions/{id}/cascade")
    public ResponseEntity<Map<String, Boolean>> deleteTransactionCascade(@PathVariable Integer id) {
        boolean deleted = transactionService.deleteTransactionCascade(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", deleted);

        return deleted ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/transactions/{id}/type")
    public ResponseEntity<?> updateTransactionType(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> requestBody) {

        Employee currentEmployee = getCurrentEmployee();
        if (currentEmployee == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String newType = (String) requestBody.get("newType");
        String notes = (String) requestBody.get("notes");

        // Odczytanie ceny końcowej, jeśli istnieje
        BigDecimal finalPrice = null;
        if (requestBody.containsKey("finalPrice")) {
            try {
                Object finalPriceObj = requestBody.get("finalPrice");
                if (finalPriceObj instanceof Number) {
                    finalPrice = BigDecimal.valueOf(((Number) finalPriceObj).doubleValue());
                } else if (finalPriceObj instanceof String) {
                    finalPrice = new BigDecimal((String) finalPriceObj);
                }
            } catch (Exception e) {
                // Ignoruj błędy konwersji
            }
        }

        // Odczytanie ID pracownika, jeśli istnieje
        Integer employeeId = null;
        if (requestBody.containsKey("employeeId")) {
            try {
                Object empIdObj = requestBody.get("employeeId");
                if (empIdObj instanceof Number) {
                    employeeId = ((Number) empIdObj).intValue();
                } else if (empIdObj instanceof String) {
                    employeeId = Integer.parseInt((String) empIdObj);
                }
            } catch (Exception e) {
                // Ignoruj błędy konwersji
            }
        }

        // Znajdź pracownika na podstawie ID, lub użyj bieżącego pracownika
        Employee saleEmployee = currentEmployee;
        if (employeeId != null) {
            Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
            if (employeeOpt.isPresent()) {
                saleEmployee = employeeOpt.get();
            }
        }

        TransactionResponse response = transactionService.updateTransactionType(id, newType, notes, finalPrice, saleEmployee);
        return handleTransactionResponse(response);
    }

    private ResponseEntity<?> handleTransactionResponse(TransactionResponse response){
        if (response.isSuccess()) {
            return new ResponseEntity<>(mapper.toTransactionDTO(response.transaction()), HttpStatus.CREATED);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", response.errorMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}