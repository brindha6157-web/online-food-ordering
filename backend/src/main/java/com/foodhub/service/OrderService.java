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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * OrderService
 * Manages order processing, transactional consistency, and payment record linkage.
 * Demonstrates: Spring Transactional semantics, Stream API reductions, ID generation.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    private final Random random = new Random();

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        MenuRepository menuRepository,
                        UserRepository userRepository,
                        PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Generates a unique readable Order Number (e.g. ORD-20261118-8421).
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randNum = 1000 + random.nextInt(9000);
        return new StringBuilder("ORD-")
                .append(timestamp)
                .append("-")
                .append(randNum)
                .toString();
    }

    /**
     * Generates a simulated Payment Reference (e.g. TXN-UPI-20261118-91823).
     */
    private String generateTransactionRef(String method) {
        String prefix = "TXN-" + method.toUpperCase() + "-";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randNum = 10000 + random.nextInt(90000);
        return prefix + datePart + "-" + randNum;
    }

    /**
     * Creates and places an order with atomic database transaction.
     */
    @Transactional
    public Order placeOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order cannot be empty. Please select food items.");
        }

        // 1. Initialize Order Master
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PLACED);
        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setOrderDate(LocalDateTime.now());

        // 2. Process Order Items and Calculate Totals using actual DB prices
        List<OrderItem> items = new ArrayList<>();
        BigDecimal computedTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemReq.getMenuItemId()));

            if (!menuItem.getIsAvailable()) {
                throw new BadRequestException("Item '" + menuItem.getName() + "' is currently out of stock.");
            }

            OrderItem orderItem = new OrderItem(order, menuItem, itemReq.getQuantity(), menuItem.getPrice());
            items.add(orderItem);
            computedTotal = computedTotal.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(computedTotal);
        order.setItems(items);

        // 3. Save Order and Items (Cascade persists items)
        Order savedOrder = orderRepository.save(order);

        // 4. Save Simulated Payment Record
        Payment.PaymentMethod pMethod;
        try {
            pMethod = Payment.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
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

    public Order getOrderById(Long orderId, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(requestingUserId)) {
            throw new UnauthorizedException("Access denied: You cannot view another customer's order.");
        }
        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(newStatus.toUpperCase());
            order.setStatus(status);
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + newStatus + ". Valid statuses: PLACED, PREPARING, COMPLETED, CANCELLED");
        }
    }

    public long countOrders() {
        return orderRepository.count();
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.calculateTotalRevenue();
    }
}
