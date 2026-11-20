package com.foodhub.controller;

import com.foodhub.dto.CustomerSummaryDto;
import com.foodhub.entity.User;
import com.foodhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CustomerController
 * Exposes administrative endpoints for Customer Management System:
 * customer listings, search & filter, detailed order history, and account status toggling.
 */
@RestController
@RequestMapping("/api/admin/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final UserService userService;

    @Autowired
    public CustomerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerSummaryDto>> getAllCustomers(
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false) String search) {
        List<CustomerSummaryDto> customers = userService.getCustomerSummaries(status, search);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCustomerDetails(@PathVariable Long id) {
        Map<String, Object> details = userService.getCustomerDetails(id);
        return ResponseEntity.ok(details);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<User> updateCustomerStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        User updated = userService.updateCustomerStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
