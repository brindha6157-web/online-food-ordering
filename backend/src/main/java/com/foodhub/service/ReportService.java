package com.foodhub.service;

import com.foodhub.dto.ReportSummaryDto;
import com.foodhub.entity.Order;
import com.foodhub.entity.OrderItem;
import com.foodhub.repository.MenuRepository;
import com.foodhub.repository.OrderRepository;
import com.foodhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ReportService
 * Generates analytical sales summaries for the Admin Dashboard.
 * Demonstrates: Java Stream API (Collectors.groupingBy, Collectors.reducing, sorted).
 */
@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    @Autowired
    public ReportService(OrderRepository orderRepository,
                         UserRepository userRepository,
                         MenuRepository menuRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
    }

    public ReportSummaryDto getSummary() {
        long totalOrders = orderRepository.count();
        long totalCustomers = userRepository.countByRole(com.foodhub.entity.User.Role.CUSTOMER);
        BigDecimal totalRevenue = orderRepository.calculateTotalCompletedRevenue();
        long availableItems = menuRepository.countByIsAvailableTrue();

        return new ReportSummaryDto(totalOrders, totalCustomers, totalRevenue, availableItems);
    }

    public List<Map<String, Object>> getDailyRevenue() {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED && o.getStatus() != Order.OrderStatus.PENDING)
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Group orders by date and sum revenue
        Map<String, List<Order>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderDate().format(formatter)));

        List<Map<String, Object>> result = new ArrayList<>();
        ordersByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    BigDecimal dailyRevenue = entry.getValue().stream()
                            .map(Order::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Map<String, Object> map = new HashMap<>();
                    map.put("date", entry.getKey());
                    map.put("ordersCount", entry.getValue().size());
                    map.put("revenue", dailyRevenue);
                    result.add(map);
                });

        return result;
    }

    public List<Map<String, Object>> getMonthlyRevenue() {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, List<Order>> ordersByMonth = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderDate().format(formatter)));

        List<Map<String, Object>> result = new ArrayList<>();
        ordersByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    BigDecimal monthlyRevenue = entry.getValue().stream()
                            .map(Order::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Map<String, Object> map = new HashMap<>();
                    map.put("month", entry.getKey());
                    map.put("ordersCount", entry.getValue().size());
                    map.put("revenue", monthlyRevenue);
                    result.add(map);
                });

        return result;
    }

    public List<Map<String, Object>> getPopularFoodItems() {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .toList();

        Map<String, Integer> itemQuantities = new HashMap<>();
        Map<String, BigDecimal> itemRevenues = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String itemName = item.getMenuItem().getName();
                itemQuantities.put(itemName, itemQuantities.getOrDefault(itemName, 0) + item.getQuantity());
                itemRevenues.put(itemName, itemRevenues.getOrDefault(itemName, BigDecimal.ZERO).add(item.getSubtotal()));
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        itemQuantities.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("quantitySold", entry.getValue());
                    map.put("totalRevenue", itemRevenues.get(entry.getKey()));
                    result.add(map);
                });

        return result;
    }

    public List<Map<String, Object>> getCategoryRevenue() {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .toList();

        Map<String, BigDecimal> categorySales = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String catName = item.getMenuItem().getCategory().getName();
                categorySales.put(catName, categorySales.getOrDefault(catName, BigDecimal.ZERO).add(item.getSubtotal()));
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        categorySales.forEach((cat, revenue) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("category", cat);
            map.put("revenue", revenue);
            result.add(map);
        });

        result.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        return result;
    }
}
