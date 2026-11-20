package com.foodhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * StockMovement Entity
 * Audit trail recording inventory quantity changes, restocks, sales, and cancellation restorations.
 */
@Entity
@Table(name = "stock_movements")
public class StockMovement {

    public enum MovementType {
        INITIAL,
        ORDER_SALE,
        ORDER_CANCEL_RESTORE,
        MANUAL_ADD,
        MANUAL_REDUCE,
        ADJUSTMENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuItem menuItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    @Column(name = "quantity_changed", nullable = false)
    private Integer quantityChanged;

    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;

    @Column(length = 255)
    private String reason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public StockMovement() {
    }

    public StockMovement(MenuItem menuItem, MovementType movementType, Integer quantityChanged, Integer quantityAfter, String reason) {
        this.menuItem = menuItem;
        this.movementType = movementType;
        this.quantityChanged = quantityChanged;
        this.quantityAfter = quantityAfter;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(Integer quantityChanged) {
        this.quantityChanged = quantityChanged;
    }

    public Integer getQuantityAfter() {
        return quantityAfter;
    }

    public void setQuantityAfter(Integer quantityAfter) {
        this.quantityAfter = quantityAfter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
