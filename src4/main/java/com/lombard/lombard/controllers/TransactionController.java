package com.lombard.lombard.controllers;

import com.lombard.lombard.repositories.TransactionRepository;
import com.lombard.lombard.dto.transaction.*;
import com.lombard.lombard.models.Customer;
import com.lombard.lombard.models.Employee;
import com.lombard.lombard.models.Transaction;
import com.lombard.lombard.models.Transaction.TransactionType;
import com.lombard.lombard.repositories.EmployeeRepository;
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
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public TransactionController(
            TransactionService transactionService,
            CustomerService customerService,
            EmployeeRepository employeeRepository,
            JwtTokenUtil jwtTokenUtil,
            Mapper mapper) {
        this.transactionService = transactionService;
        this.customerService = customerService;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
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
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Autowired
    private TransactionRepository transactionRepository;
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


    private Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return employeeRepository.findByLogin(username).orElse(null);
        }
        return null;
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
