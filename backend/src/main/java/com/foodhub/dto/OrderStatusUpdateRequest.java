package com.foodhub.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderStatusUpdateRequest {

    @NotBlank(message = "Status is required (PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)")
    private String status;

    private String remarks;

    private String changedBy = "ADMIN";

    public OrderStatusUpdateRequest() {
    }

    public OrderStatusUpdateRequest(String status) {
        this.status = status;
    }

    public OrderStatusUpdateRequest(String status, String remarks, String changedBy) {
        this.status = status;
        this.remarks = remarks;
        this.changedBy = changedBy != null ? changedBy : "ADMIN";
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
}
