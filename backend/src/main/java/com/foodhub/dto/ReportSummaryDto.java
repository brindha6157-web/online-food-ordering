package com.foodhub.dto;

import java.math.BigDecimal;

public class ReportSummaryDto {
    private long totalOrders;
    private long totalCustomers;
    private BigDecimal totalRevenue;
    private long availableFoodItems;

    public ReportSummaryDto() {
    }

    public ReportSummaryDto(long totalOrders, long totalCustomers, BigDecimal totalRevenue, long availableFoodItems) {
        this.totalOrders = totalOrders;
        this.totalCustomers = totalCustomers;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.availableFoodItems = availableFoodItems;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getAvailableFoodItems() {
        return availableFoodItems;
    }

    public void setAvailableFoodItems(long availableFoodItems) {
        this.availableFoodItems = availableFoodItems;
    }
}
