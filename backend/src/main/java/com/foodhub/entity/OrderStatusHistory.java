package com.foodhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * OrderStatusHistory Entity
 * Tracks lifecycle status transitions and audit notes for an order.
 */
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(length = 255)
    private String remarks;

    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderStatusHistory() {
    }

    public OrderStatusHistory(Long orderId, String status, String remarks, String changedBy) {
        this.orderId = orderId;
        this.status = status;
        this.remarks = remarks;
        this.changedBy = changedBy != null ? changedBy : "SYSTEM";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
