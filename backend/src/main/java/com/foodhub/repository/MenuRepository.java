package com.foodhub.repository;

import com.foodhub.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryId(Long categoryId);
    List<MenuItem> findByIsAvailableTrue();
    List<MenuItem> findByNameContainingIgnoreCase(String name);
    long countByIsAvailableTrue();

    @Query("SELECT m FROM MenuItem m WHERE m.category.id = :categoryId AND m.isAvailable = true")
    List<MenuItem> findAvailableByCategoryId(Long categoryId);

    @Query("SELECT m FROM MenuItem m WHERE m.stockQuantity <= m.minStockLevel AND m.stockQuantity > 0")
    List<MenuItem> findLowStockItems();

    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.stockQuantity <= m.minStockLevel AND m.stockQuantity > 0")
    long countLowStockItems();

    @Query("SELECT m FROM MenuItem m WHERE m.stockQuantity = 0")
    List<MenuItem> findOutOfStockItems();

    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.stockQuantity = 0")
    long countOutOfStockItems();

    @Query("SELECT COALESCE(SUM(m.stockQuantity), 0) FROM MenuItem m")
    long sumAvailableStock();

    @Query("SELECT COALESCE(SUM(m.price * m.stockQuantity), 0) FROM MenuItem m")
    BigDecimal calculateTotalStockValue();

    @Query("SELECT m FROM MenuItem m WHERE " +
           "(:categoryId IS NULL OR m.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.id ASC")
    List<MenuItem> searchAndFilterMenu(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);
}
