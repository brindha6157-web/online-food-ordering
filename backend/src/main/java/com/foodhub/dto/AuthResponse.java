package com.foodhub.dto;

import com.foodhub.entity.User;

public class AuthResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String address;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        if (user != null) {
            this.userId = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.role = user.getRole().name();
            this.phone = user.getPhone();
            this.address = user.getAddress();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
}
