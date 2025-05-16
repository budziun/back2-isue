package com.lombard.lombard.dto.transaction;

import java.math.BigDecimal;

public class SaleItemDTO {
    private Integer itemId;
    private BigDecimal sellingPrice;

    // Getters and Setters
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}