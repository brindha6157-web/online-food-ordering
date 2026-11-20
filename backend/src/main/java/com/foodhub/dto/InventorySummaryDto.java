package com.foodhub.dto;

import java.math.BigDecimal;

public class InventorySummaryDto {

    private Long totalProducts;
    private Long availableStock;
    private Long lowStockProducts;
    private Long outOfStockProducts;
    private BigDecimal stockValue;

    public InventorySummaryDto() {
    }

    public InventorySummaryDto(Long totalProducts, Long availableStock, Long lowStockProducts, Long outOfStockProducts, BigDecimal stockValue) {
        this.totalProducts = totalProducts;
        this.availableStock = availableStock;
        this.lowStockProducts = lowStockProducts;
        this.outOfStockProducts = outOfStockProducts;
        this.stockValue = stockValue;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Long getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Long availableStock) {
        this.availableStock = availableStock;
    }

    public Long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(Long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public Long getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public void setOutOfStockProducts(Long outOfStockProducts) {
        this.outOfStockProducts = outOfStockProducts;
    }

    public BigDecimal getStockValue() {
        return stockValue;
    }

    public void setStockValue(BigDecimal stockValue) {
        this.stockValue = stockValue;
    }
}
