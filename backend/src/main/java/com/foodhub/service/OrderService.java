package com.foodhub.service;

import com.foodhub.dto.OrderItemRequest;
import com.foodhub.dto.OrderRequest;
import com.foodhub.entity.*;
import com.foodhub.exception.BadRequestException;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.exception.UnauthorizedException;
import com.foodhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * OrderService
 * Manages atomic order placement, inventory deductions, status transitions, cancellation rules,
 * stock restorations, and complete lifecycle audit trails.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final StockMovementRepository stockMovementRepository;

    private final Random random = new Random();

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        MenuRepository menuRepository,
                        UserRepository userRepository,
                        PaymentRepository paymentRepository,
                        OrderStatusHistoryRepository orderStatusHistoryRepository,
                        StockMovementRepository stockMovementRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randNum = 1000 + random.nextInt(9000);
        return "ORD-" + timestamp + "-" + randNum;
    }

    private String generateTransactionRef(String method) {
        String prefix = "TXN-" + method.toUpperCase() + "-";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randNum = 10000 + random.nextInt(90000);
        return prefix + datePart + "-" + randNum;
    }

    /**
     * Atomically places an order, validates stock availability, deducts inventory,
     * logs stock movements, creates simulated payment, and logs initial status history.
     */
    @Transactional
    public Order placeOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (user.getStatus() == User.Status.INACTIVE) {
            throw new BadRequestException("Your account is currently inactive. Please contact support.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order cannot be empty. Please select food items.");
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setOrderDate(LocalDateTime.now());
        order.setStockRestored(false);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal computedTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemReq.getMenuItemId()));

            if (!Boolean.TRUE.equals(menuItem.getIsAvailable()) || menuItem.getStockQuantity() == null || menuItem.getStockQuantity() <= 0) {
                throw new BadRequestException("Item '" + menuItem.getName() + "' is currently out of stock.");
            }

            if (menuItem.getStockQuantity() < itemReq.getQuantity()) {
                throw new BadRequestException("Insufficient stock for '" + menuItem.getName() +
                        "'. Requested: " + itemReq.getQuantity() + ", Available: " + menuItem.getStockQuantity());
            }

            // Deduct inventory atomically
            int updatedStock = menuItem.getStockQuantity() - itemReq.getQuantity();
            menuItem.setStockQuantity(updatedStock);
            if (updatedStock <= 0) {
                menuItem.setIsAvailable(false);
            }
            menuRepository.save(menuItem);

            // Record stock movement audit log
            StockMovement movement = new StockMovement(
                    menuItem,
                    StockMovement.MovementType.ORDER_SALE,
                    -itemReq.getQuantity(),
                    updatedStock,
                    "Order placed: " + order.getOrderNumber()
            );
            stockMovementRepository.save(movement);

            OrderItem orderItem = new OrderItem(order, menuItem, itemReq.getQuantity(), menuItem.getPrice());
            items.add(orderItem);
            computedTotal = computedTotal.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(computedTotal);
        order.setItems(items);

        Order savedOrder = orderRepository.save(order);

        // Record Initial Order Status History
        OrderStatusHistory history = new OrderStatusHistory(
                savedOrder.getId(),
                Order.OrderStatus.PENDING.name(),
                "Order received and awaiting kitchen confirmation",
                "CUSTOMER"
        );
        orderStatusHistoryRepository.save(history);

        // Record Simulated Payment
        Payment.PaymentMethod pMethod;
        try {
            pMethod = Payment.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (Exception e) {
            pMethod = Payment.PaymentMethod.UPI;
        }

        Payment payment = new Payment(
                savedOrder,
                pMethod,
                generateTransactionRef(pMethod.name()),
                computedTotal,
                Payment.PaymentStatus.SUCCESS
        );
        paymentRepository.save(payment);

        return savedOrder;
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public List<Order> getOrdersByStatus(String statusStr) {
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            return orderRepository.findByStatusOrderByOrderDateDesc(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + statusStr);
        }
    }

    public List<Order> getCancelledOrders() {
        return orderRepository.findByStatusOrderByOrderDateDesc(Order.OrderStatus.CANCELLED);
    }

    public Order getOrderById(Long orderId, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(requestingUserId)) {
            throw new UnauthorizedException("Access denied: You cannot view another customer's order.");
        }
        return order;
    }

    /**
     * Updates an order's status and logs to the status history.
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatusStr, String remarks, String changedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        Order.OrderStatus targetStatus;
        try {
            targetStatus = Order.OrderStatus.valueOf(newStatusStr.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + newStatusStr +
                    ". Valid statuses: PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED");
        }

        if (order.getStatus() == Order.OrderStatus.DELIVERED && targetStatus != Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot change status of an already delivered order.");
        }

        if (targetStatus == Order.OrderStatus.CANCELLED) {
            return cancelOrderInternal(order, remarks != null ? remarks : "Order cancelled by administrator",
                    changedBy != null ? changedBy : "ADMIN", true);
        }

        order.setStatus(targetStatus);
        Order updated = orderRepository.save(order);

        OrderStatusHistory history = new OrderStatusHistory(
                order.getId(),
                targetStatus.name(),
                remarks != null ? remarks : "Status updated to " + targetStatus.name(),
                changedBy != null ? changedBy : "ADMIN"
        );
        orderStatusHistoryRepository.save(history);

        return updated;
    }

    /**
     * Cancels an order, enforces boundary conditions, restores stock exactly once,
     * updates payment status, and saves audit history.
     */
    @Transactional
    public Order cancelOrder(Long orderId, String reason, String cancelledBy, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!isAdmin) {
            if (requestingUserId != null && !order.getUser().getId().equals(requestingUserId)) {
                throw new UnauthorizedException("Unauthorized access: You can only cancel your own orders.");
            }
            if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.CONFIRMED) {
                throw new BadRequestException("Orders can only be cancelled while in 'PENDING' or 'CONFIRMED' status. " +
                        "Current status: " + order.getStatus());
            }
        }

        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel an order that has already been delivered.");
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled.");
        }

        return cancelOrderInternal(order, reason, cancelledBy, isAdmin);
    }

    private Order cancelOrderInternal(Order order, String reason, String cancelledBy, boolean isAdmin) {
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancellationReason(reason != null && !reason.trim().isEmpty() ? reason.trim() : "Cancelled by user");
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelledBy(cancelledBy != null ? cancelledBy : (isAdmin ? "ADMIN" : "CUSTOMER"));

        // Restore product stock exactly once
        if (!Boolean.TRUE.equals(order.getStockRestored())) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem oi : items) {
                MenuItem mi = oi.getMenuItem();
                int restoredQuantity = mi.getStockQuantity() + oi.getQuantity();
                mi.setStockQuantity(restoredQuantity);
                if (restoredQuantity > 0) {
                    mi.setIsAvailable(true);
                }
                menuRepository.save(mi);

                StockMovement movement = new StockMovement(
                        mi,
                        StockMovement.MovementType.ORDER_CANCEL_RESTORE,
                        oi.getQuantity(),
                        restoredQuantity,
                        "Stock restored from cancelled order " + order.getOrderNumber() + ": " + order.getCancellationReason()
                );
                stockMovementRepository.save(movement);
            }
            order.setStockRestored(true);
        }

        // Refund simulated payment
        paymentRepository.findByOrderId(order.getId()).ifPresent(payment -> {
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        });

        Order saved = orderRepository.save(order);

        OrderStatusHistory history = new OrderStatusHistory(
                saved.getId(),
                Order.OrderStatus.CANCELLED.name(),
                order.getCancellationReason(),
                order.getCancelledBy()
        );
        orderStatusHistoryRepository.save(history);

        return saved;
    }

    public List<OrderStatusHistory> getOrderHistory(Long orderId) {
        return orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
    }

    public List<Order> filterAndSearchOrders(String statusStr, String startDateStr, String endDateStr, String keyword) {
        Order.OrderStatus status = null;
        if (statusStr != null && !statusStr.trim().isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
            try {
                status = Order.OrderStatus.valueOf(statusStr.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException ignored) {}
        }

        LocalDateTime startDate = null;
        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            startDate = LocalDate.parse(startDateStr.trim()).atStartOfDay();
        }

        LocalDateTime endDate = null;
        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            endDate = LocalDate.parse(endDateStr.trim()).atTime(LocalTime.MAX);
        }

        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        return orderRepository.filterAndSearchOrders(status, startDate, endDate, kw);
    }

    public long countOrders() {
        return orderRepository.count();
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.calculateTotalCompletedRevenue();
    }
}
