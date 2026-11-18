package com.foodhub.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderStatusUpdateRequest {

    @NotBlank(message = "Status is required (PLACED, PREPARING, COMPLETED, CANCELLED)")
    private String status;

    public OrderStatusUpdateRequest() {
    }

    public OrderStatusUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
