package com.lombard.lombard.repositories;

import com.lombard.lombard.models.Category;
import com.lombard.lombard.models.Item;
import com.lombard.lombard.models.Item.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByStatus(ItemStatus status);

    List<Item> findByCategory(Category category);

    @Query("SELECT i FROM Item i WHERE (i.name LIKE %?1% OR i.description LIKE %?1% OR i.brand LIKE %?1% OR i.model LIKE %?1%)")
    List<Item> searchByKeyword(String keyword);

    List<Item> findByStatusAndReportedStolenFalse(ItemStatus status);

    List<Item> findByAskingPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT i FROM Item i WHERE i.status = 'in_inventory' OR i.status = 'forfeited'")
    List<Item> findItemsAvailableForSale();
}