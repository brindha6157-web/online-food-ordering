package com.foodhub.controller;

import com.foodhub.dto.OrderItemRequest;
import com.foodhub.dto.OrderRequest;
import com.foodhub.dto.OrderStatusUpdateRequest;
import com.foodhub.entity.Order;
import com.foodhub.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OrderController
 * Exposes customer cart checkout and admin order management endpoints.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest request) {
        Order placed = orderService.placeOrder(request);
        return ResponseEntity.ok(placed);
    }

    @GetMapping("/orders/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(@RequestParam Long userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id,
                                              @RequestParam(required = false) Long userId,
                                              @RequestParam(defaultValue = "false") boolean isAdmin) {
        Order order = orderService.getOrderById(id, userId, isAdmin);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<List<Order>> getAllOrdersForAdmin() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/admin/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
                                                   @Valid @RequestBody OrderStatusUpdateRequest request) {
        Order updated = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }
}
