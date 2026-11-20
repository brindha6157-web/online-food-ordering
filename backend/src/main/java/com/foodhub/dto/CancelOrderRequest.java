package com.foodhub.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelOrderRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    private String cancelledBy = "CUSTOMER";

    public CancelOrderRequest() {
    }

    public CancelOrderRequest(String reason, String cancelledBy) {
        this.reason = reason;
        this.cancelledBy = cancelledBy != null ? cancelledBy : "CUSTOMER";
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
}
