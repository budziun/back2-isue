package com.lombard.lombard.repositories;

import com.lombard.lombard.models.Customer;
import com.lombard.lombard.models.Transaction;
import com.lombard.lombard.models.Transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByCustomer(Customer customer);

    List<Transaction> findByTransactionType(TransactionType transactionType);

    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByCustomerAndTransactionType(Customer customer, TransactionType transactionType);

    @Query("SELECT t FROM Transaction t WHERE t.transactionType = 'pawn' AND t.expiryDate < ?1 AND NOT EXISTS " +
            "(SELECT r FROM Transaction r WHERE r.relatedTransaction = t AND " +
            "(r.transactionType = 'redemption' OR r.transactionType = 'forfeiture'))")
    List<Transaction> findUnredeemedExpiredPawns(LocalDate currentDate);

    @Query("SELECT t FROM Transaction t WHERE t.transactionType = 'pawn' AND t.customer.id = ?1 AND NOT EXISTS " +
            "(SELECT r FROM Transaction r WHERE r.relatedTransaction = t AND " +
            "(r.transactionType = 'redemption' OR r.transactionType = 'forfeiture'))")
    List<Transaction> findActivePawnsByCustomerId(Integer customerId);

    // NOWY ENDPOINT: transakcje zawierające itemy z danej kategorii
    @Query("SELECT DISTINCT t FROM Transaction t JOIN t.transactionItems i WHERE i.item.category.id = :categoryId")
    List<Transaction> findByItemCategoryId(Integer categoryId);
}
