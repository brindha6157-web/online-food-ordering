package com.foodhub.controller;

import com.foodhub.dto.CancelOrderRequest;
import com.foodhub.dto.OrderRequest;
import com.foodhub.dto.OrderStatusUpdateRequest;
import com.foodhub.entity.Order;
import com.foodhub.entity.OrderStatusHistory;
import com.foodhub.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OrderController
 * Exposes customer cart checkout, order cancellation, tracking timeline,
 * and comprehensive administrative order management endpoints.
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

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id,
                                             @RequestBody CancelOrderRequest request,
                                             @RequestParam(required = false) Long userId,
                                             @RequestParam(defaultValue = "false") boolean isAdmin) {
        String reason = request != null ? request.getReason() : "Cancelled by user";
        String cancelledBy = (request != null && request.getCancelledBy() != null)
                ? request.getCancelledBy()
                : (isAdmin ? "ADMIN" : "CUSTOMER");

        Order cancelled = orderService.cancelOrder(id, reason, cancelledBy, userId, isAdmin);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/orders/{id}/history")
    public ResponseEntity<List<OrderStatusHistory>> getOrderHistory(@PathVariable Long id) {
        List<OrderStatusHistory> history = orderService.getOrderHistory(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<List<Order>> getAllOrdersForAdmin(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search) {
        List<Order> orders = orderService.filterAndSearchOrders(status, startDate, endDate, search);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/admin/orders/cancelled")
    public ResponseEntity<List<Order>> getCancelledOrders() {
        List<Order> orders = orderService.getCancelledOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/admin/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
                                                   @Valid @RequestBody OrderStatusUpdateRequest request) {
        Order updated = orderService.updateOrderStatus(id, request.getStatus(), request.getRemarks(), request.getChangedBy());
        return ResponseEntity.ok(updated);
    }
}
