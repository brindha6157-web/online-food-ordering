package com.foodhub.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerSummaryDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;
    private String status;
    private LocalDateTime registrationDate;
    private Long totalOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private BigDecimal totalSpending;
    private LocalDateTime latestOrderDate;

    public CustomerSummaryDto() {
    }

    public CustomerSummaryDto(Long id, String name, String email, String phone, String address, String role,
                              String status, LocalDateTime registrationDate, Long totalOrders, Long completedOrders,
                              Long cancelledOrders, BigDecimal totalSpending, LocalDateTime latestOrderDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.status = status;
        this.registrationDate = registrationDate;
        this.totalOrders = totalOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalSpending = totalSpending;
        this.latestOrderDate = latestOrderDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(Long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public BigDecimal getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(BigDecimal totalSpending) {
        this.totalSpending = totalSpending;
    }

    public LocalDateTime getLatestOrderDate() {
        return latestOrderDate;
    }

    public void setLatestOrderDate(LocalDateTime latestOrderDate) {
        this.latestOrderDate = latestOrderDate;
    }
}
