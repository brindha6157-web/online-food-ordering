package com.foodhub.repository;

import com.foodhub.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryId(Long categoryId);
    List<MenuItem> findByIsAvailableTrue();
    List<MenuItem> findByNameContainingIgnoreCase(String name);
    long countByIsAvailableTrue();

    @Query("SELECT m FROM MenuItem m WHERE m.category.id = :categoryId AND m.isAvailable = true")
    List<MenuItem> findAvailableByCategoryId(Long categoryId);
}
