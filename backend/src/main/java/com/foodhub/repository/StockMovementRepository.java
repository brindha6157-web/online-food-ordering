package com.foodhub.repository;

import com.foodhub.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByMenuItem_IdOrderByCreatedAtDesc(Long menuItemId);
    List<StockMovement> findAllByOrderByCreatedAtDesc();
}
