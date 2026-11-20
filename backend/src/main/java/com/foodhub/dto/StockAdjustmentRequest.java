package com.foodhub.dto;

import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotNull(message = "Quantity change is required")
    private Integer quantityChanged;

    private String movementType = "ADJUSTMENT";

    private String reason;

    public StockAdjustmentRequest() {
    }

    public StockAdjustmentRequest(Integer quantityChanged, String movementType, String reason) {
        this.quantityChanged = quantityChanged;
        this.movementType = movementType != null ? movementType : "ADJUSTMENT";
        this.reason = reason;
    }

    public Integer getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(Integer quantityChanged) {
        this.quantityChanged = quantityChanged;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
