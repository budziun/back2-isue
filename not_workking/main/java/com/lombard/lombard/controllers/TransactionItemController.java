package com.lombard.lombard.controllers;

import com.lombard.lombard.dto.transaction.TransactionItemDTO;
import com.lombard.lombard.models.TransactionItem;
import com.lombard.lombard.repositories.TransactionItemRepository;
import com.lombard.lombard.utils.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class TransactionItemController {

    private final TransactionItemRepository itemRepository;
    private final Mapper mapper;

    public TransactionItemController(TransactionItemRepository itemRepository, Mapper mapper) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<TransactionItemDTO>> getItemsByCategory(@PathVariable Integer categoryId) {
        List<TransactionItem> items = itemRepository.findByCategoryId(categoryId);
        List<TransactionItemDTO> dtos = items.stream().map(mapper::toTransactionItemDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
