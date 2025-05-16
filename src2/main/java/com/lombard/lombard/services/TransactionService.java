package com.lombard.lombard.services;

import com.lombard.lombard.dto.customer.CreateCustomerDTO;
import com.lombard.lombard.dto.transaction.*;
import com.lombard.lombard.models.*;
import com.lombard.lombard.models.Item.ItemStatus;
import com.lombard.lombard.models.Transaction.TransactionType;
import com.lombard.lombard.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionItemRepository transactionItemRepository,
            ItemRepository itemRepository,
            CategoryRepository categoryRepository,
            CustomerRepository customerRepository,
            CustomerService customerService) {
        this.transactionRepository = transactionRepository;
        this.transactionItemRepository = transactionItemRepository;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }


    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Integer id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByCustomer(Customer customer) {
        return transactionRepository.findByCustomer(customer);
    }

    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByTransactionType(type);
    }

    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate);
    }

    public List<Transaction> getActivePawnsByCustomerId(Integer customerId) {
        return transactionRepository.findActivePawnsByCustomerId(customerId);
    }


    @Transactional
    public TransactionResponse createPurchaseTransaction(CreatePurchaseDTO purchaseDTO, Employee employee) {
        try {

            Customer customer = getOrCreateCustomer(purchaseDTO.getCustomerId(), purchaseDTO.getNewCustomer());
            if (customer == null) {
                return new TransactionResponse(null, "customer information required");
            }


            if (customer.isDoNotServe()) {
                return new TransactionResponse(null, "do not server this customer");
            }


            List<Item> items = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            if (purchaseDTO.getItems() == null || purchaseDTO.getItems().isEmpty()) {
                return new TransactionResponse(null, "no items in purchase");
            }

            for (PurchaseItemDTO itemDTO : purchaseDTO.getItems()) {
                Optional<Category> categoryOpt = categoryRepository.findById(itemDTO.getCategoryId());
                if (categoryOpt.isEmpty()) {
                    return new TransactionResponse(null, "category not found");
                }

                Item item = new Item();
                item.setCategory(categoryOpt.get());
                item.setName(itemDTO.getName());
                item.setDescription(itemDTO.getDescription());
                item.setSerialNumber(itemDTO.getSerialNumber());
                item.setBrand(itemDTO.getBrand());
                item.setModel(itemDTO.getModel());
                item.setCondition(itemDTO.getCondition());
                item.setBoughtFor(itemDTO.getBoughtFor());
                item.setAskingPrice(itemDTO.getAskingPrice());
                item.setStatus(ItemStatus.in_inventory);
                item.setCreatedBy(employee);
                item.setUpdatedBy(employee);

                items.add(item);
                totalAmount = totalAmount.add(itemDTO.getBoughtFor());
            }


            Transaction transaction = new Transaction();
            transaction.setCustomer(customer);
            transaction.setEmployee(employee);
            transaction.setTransactionType(TransactionType.purchase);
            transaction.setTotalAmount(totalAmount);
            transaction.setNotes(purchaseDTO.getNotes());


            Transaction savedTransaction = transactionRepository.save(transaction);

            for (Item item : items) {
                Item savedItem = itemRepository.save(item);

                TransactionItem transactionItem = new TransactionItem();
                transactionItem.setTransaction(savedTransaction);
                transactionItem.setItem(savedItem);
                transactionItem.setPrice(savedItem.getBoughtFor());
                transactionItemRepository.save(transactionItem);
            }

            return new TransactionResponse(savedTransaction, null);
        } catch (Exception e) {
            return new TransactionResponse(null, e.getMessage());
        }
    }


    @Transactional
    public TransactionResponse createSaleTransaction(CreateSaleDTO saleDTO, Employee employee) {
        try {

            Customer customer = null;
            if (saleDTO.getCustomerId() != null) {
                Optional<Customer> customerOpt = customerRepository.findById(saleDTO.getCustomerId());
                if (customerOpt.isEmpty()) {
                    return new TransactionResponse(null, "customer not found");
                }
                customer = customerOpt.get();

                if (customer.isDoNotServe()) {
                    return new TransactionResponse(null, "do not server this customer");
                }
            }

            if (saleDTO.getItems() == null || saleDTO.getItems().isEmpty()) {
                return new TransactionResponse(null, "no items in sale");
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<Item> itemsToUpdate = new ArrayList<>();

            for (SaleItemDTO itemDTO : saleDTO.getItems()) {
                Optional<Item> itemOpt = itemRepository.findById(itemDTO.getItemId());
                if (itemOpt.isEmpty()) {
                    return new TransactionResponse(null, "item not found");
                }

                Item item = itemOpt.get();

                if (item.getStatus() != ItemStatus.in_inventory && item.getStatus() != ItemStatus.forfeited) {
                    return new TransactionResponse(null, "item not available for sale");
                }

                if (item.isReportedStolen()) {
                    return new TransactionResponse(null, "item reported stolen");
                }

                totalAmount = totalAmount.add(itemDTO.getSellingPrice());
                item.setStatus(ItemStatus.sold);
                item.setUpdatedBy(employee);
                itemsToUpdate.add(item);
            }

            Transaction transaction = new Transaction();
            transaction.setCustomer(customer);
            transaction.setEmployee(employee);
            transaction.setTransactionType(TransactionType.sale);
            transaction.setTotalAmount(totalAmount);
            transaction.setNotes(saleDTO.getNotes());

            Transaction savedTransaction = transactionRepository.save(transaction);

            for (int i = 0; i < itemsToUpdate.size(); i++) {
                Item item = itemsToUpdate.get(i);
                Item savedItem = itemRepository.save(item);

                TransactionItem transactionItem = new TransactionItem();
                transactionItem.setTransaction(savedTransaction);
                transactionItem.setItem(savedItem);
                transactionItem.setPrice(saleDTO.getItems().get(i).getSellingPrice());
                transactionItemRepository.save(transactionItem);
            }

            return new TransactionResponse(savedTransaction, null);
        } catch (Exception e) {
            return new TransactionResponse(null, e.getMessage());
        }
    }


    @Transactional
    public TransactionResponse createPawnTransaction(CreatePawnDTO pawnDTO, Employee employee) {
        try {
            Customer customer = getOrCreateCustomer(pawnDTO.getCustomerId(), pawnDTO.getNewCustomer());
            if (customer == null) {
                return new TransactionResponse(null, "customer information required for pawning");
            }

            if (customer.isDoNotServe()) {
                return new TransactionResponse(null, "do not server this customer'");
            }

            if (pawnDTO.getPawnDurationDays() == null || pawnDTO.getPawnDurationDays() <= 0) {
                return new TransactionResponse(null, "valid pawn duration required");
            }

            if (pawnDTO.getInterestRate() == null || pawnDTO.getInterestRate().compareTo(BigDecimal.ZERO) <= 0) {
                return new TransactionResponse(null, "valid interest rate required");
            }

            List<Item> items = new ArrayList<>();
            BigDecimal totalLoanAmount = BigDecimal.ZERO;

            if (pawnDTO.getItems() == null || pawnDTO.getItems().isEmpty()) {
                return new TransactionResponse(null, "no items in pawn");
            }

            for (PawnItemDTO itemDTO : pawnDTO.getItems()) {
                Optional<Category> categoryOpt = categoryRepository.findById(itemDTO.getCategoryId());
                if (categoryOpt.isEmpty()) {
                    return new TransactionResponse(null, "category not found");
                }

                Item item = new Item();
                item.setCategory(categoryOpt.get());
                item.setName(itemDTO.getName());
                item.setDescription(itemDTO.getDescription());
                item.setSerialNumber(itemDTO.getSerialNumber());
                item.setBrand(itemDTO.getBrand());
                item.setModel(itemDTO.getModel());
                item.setCondition(itemDTO.getCondition());
                item.setStatus(ItemStatus.pawned);
                item.setCreatedBy(employee);
                item.setUpdatedBy(employee);

                items.add(item);
                totalLoanAmount = totalLoanAmount.add(itemDTO.getLoanAmount());
            }

            LocalDate expiryDate = LocalDate.now().plusDays(pawnDTO.getPawnDurationDays());
            BigDecimal interestAmount = totalLoanAmount.multiply(pawnDTO.getInterestRate().divide(new BigDecimal("100")));
            BigDecimal redemptionPrice = totalLoanAmount.add(interestAmount);

            Transaction transaction = new Transaction();
            transaction.setCustomer(customer);
            transaction.setEmployee(employee);
            transaction.setTransactionType(TransactionType.pawn);
            transaction.setTotalAmount(totalLoanAmount);
            transaction.setPawnDurationDays(pawnDTO.getPawnDurationDays());
            transaction.setInterestRate(pawnDTO.getInterestRate());
            transaction.setRedemptionPrice(redemptionPrice);
            transaction.setExpiryDate(expiryDate);
            transaction.setNotes(pawnDTO.getNotes());

            Transaction savedTransaction = transactionRepository.save(transaction);

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                Item savedItem = itemRepository.save(item);

                TransactionItem transactionItem = new TransactionItem();
                transactionItem.setTransaction(savedTransaction);
                transactionItem.setItem(savedItem);
                transactionItem.setPrice(pawnDTO.getItems().get(i).getLoanAmount());
                transactionItemRepository.save(transactionItem);
            }

            return new TransactionResponse(savedTransaction, null);
        } catch (Exception e) {
            return new TransactionResponse(null, e.getMessage());
        }
    }

    @Transactional
    public TransactionResponse createRedemptionTransaction(CreateRedemptionDTO redemptionDTO, Employee employee) {
        try {
            Optional<Transaction> pawnTransactionOpt = transactionRepository.findById(redemptionDTO.getPawnTransactionId());
            if (pawnTransactionOpt.isEmpty()) {
                return new TransactionResponse(null, "original pawn not found");
            }

            Transaction pawnTransaction = pawnTransactionOpt.get();

            if (pawnTransaction.getTransactionType() != TransactionType.pawn) {
                return new TransactionResponse(null, "transaction is not a pawn");
            }

            List<Transaction> relatedTransactions = transactionRepository.findAll().stream()
                    .filter(t -> pawnTransaction.equals(t.getRelatedTransaction()))
                    .toList();

            if (!relatedTransactions.isEmpty()) {
                return new TransactionResponse(null, "pawn has been redeemed or forfeited");
            }

            if (pawnTransaction.getExpiryDate().isBefore(LocalDate.now())) {
                return new TransactionResponse(null, "pawn expired and cannot be redeemed");
            }

            List<TransactionItem> pawnItems = transactionItemRepository.findByTransaction(pawnTransaction);
            List<Item> itemsToUpdate = new ArrayList<>();

            for (TransactionItem ti : pawnItems) {
                Item item = ti.getItem();
                if (item.getStatus() != ItemStatus.pawned) {
                    return new TransactionResponse(null, "Item is not pawned");
                }
                item.setStatus(ItemStatus.redeemed);
                item.setUpdatedBy(employee);
                itemsToUpdate.add(item);
            }

            Transaction redemptionTransaction = new Transaction();
            redemptionTransaction.setCustomer(pawnTransaction.getCustomer());
            redemptionTransaction.setEmployee(employee);
            redemptionTransaction.setTransactionType(TransactionType.redemption);
            redemptionTransaction.setTotalAmount(pawnTransaction.getRedemptionPrice());
            redemptionTransaction.setRelatedTransaction(pawnTransaction);
            redemptionTransaction.setNotes(redemptionDTO.getNotes());

            Transaction savedTransaction = transactionRepository.save(redemptionTransaction);

            itemRepository.saveAll(itemsToUpdate);

            for (TransactionItem pawnItem : pawnItems) {
                TransactionItem redemptionItem = new TransactionItem();
                redemptionItem.setTransaction(savedTransaction);
                redemptionItem.setItem(pawnItem.getItem());
                redemptionItem.setPrice(pawnItem.getPrice());
                transactionItemRepository.save(redemptionItem);
            }

            return new TransactionResponse(savedTransaction, null);
        } catch (Exception e) {
            return new TransactionResponse(null, e.getMessage());
        }
    }

    private Customer getOrCreateCustomer(Integer customerId, CreateCustomerDTO newCustomerDTO) {
        if (customerId != null) {
            return customerRepository.findById(customerId).orElse(null);
        }

        if (newCustomerDTO != null) {
            Customer newCustomer = new Customer();
            newCustomer.setFirstName(newCustomerDTO.getFirstName());
            newCustomer.setLastName(newCustomerDTO.getLastName());
            newCustomer.setIdType(newCustomerDTO.getIdType());
            newCustomer.setIdNumber(newCustomerDTO.getIdNumber());
            newCustomer.setDoNotServe(newCustomerDTO.isDoNotServe());
            return customerService.createCustomer(newCustomer);
        }

        return null;
    }

        public record TransactionResponse(Transaction transaction, String errorMessage) {

        public boolean isSuccess() {
                return transaction != null;
            }
        }
}