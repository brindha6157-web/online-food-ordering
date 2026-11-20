package com.foodhub.repository;

import com.foodhub.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    List<Order> findByUserIdAndStatusOrderByOrderDateDesc(Long userId, Order.OrderStatus status);
    long countByStatus(Order.OrderStatus status);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, Order.OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user.id = :userId AND o.status != 'CANCELLED'")
    BigDecimal calculateTotalSpentByUser(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status NOT IN ('CANCELLED', 'PENDING')")
    BigDecimal calculateTotalCompletedRevenue();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderDate <= :endDate) AND " +
           "(:keyword IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(o.user.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY o.orderDate DESC")
    List<Order> filterAndSearchOrders(
            @Param("status") Order.OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keyword") String keyword
    );
}
