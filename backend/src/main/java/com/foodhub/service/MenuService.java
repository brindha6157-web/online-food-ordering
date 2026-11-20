package com.foodhub.service;

import com.foodhub.dto.InventorySummaryDto;
import com.foodhub.dto.MenuItemDto;
import com.foodhub.dto.StockAdjustmentRequest;
import com.foodhub.entity.Category;
import com.foodhub.entity.MenuItem;
import com.foodhub.entity.StockMovement;
import com.foodhub.exception.BadRequestException;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.repository.CategoryRepository;
import com.foodhub.repository.MenuRepository;
import com.foodhub.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MenuService
 * Handles food catalog operations, inventory tracking, stock adjustments, and inventory movement logs.
 * Demonstrates: Java Stream API, Inventory rule validation, Atomic transactions.
 */
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final StockMovementRepository stockMovementRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository,
                       CategoryRepository categoryRepository,
                       StockMovementRepository stockMovementRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuRepository.findAll();
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsAvailable()) && item.getStockQuantity() != null && item.getStockQuantity() > 0)
                .collect(Collectors.toList());
    }

    public List<MenuItem> getItemsByCategory(Long categoryId) {
        return menuRepository.findByCategoryId(categoryId).stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsAvailable()) && item.getStockQuantity() != null && item.getStockQuantity() > 0)
                .collect(Collectors.toList());
    }

    public List<MenuItem> searchMenuItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAvailableMenuItems();
        }
        String cleanKeyword = keyword.trim().toLowerCase();
        return menuRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsAvailable()) && item.getStockQuantity() != null && item.getStockQuantity() > 0)
                .filter(item -> item.getName().toLowerCase().contains(cleanKeyword)
                        || item.getDescription().toLowerCase().contains(cleanKeyword))
                .collect(Collectors.toList());
    }

    public List<MenuItem> getItemsSortedByPrice(boolean ascending) {
        Comparator<MenuItem> comparator = Comparator.comparing(MenuItem::getPrice);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return menuRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsAvailable()) && item.getStockQuantity() != null && item.getStockQuantity() > 0)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public MenuItem getMenuItemById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    @Transactional
    public MenuItem createMenuItem(MenuItemDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        int initialStock = dto.getStockQuantity() != null ? Math.max(0, dto.getStockQuantity()) : 50;
        int minLevel = dto.getMinStockLevel() != null ? Math.max(0, dto.getMinStockLevel()) : 10;
        boolean available = dto.getIsAvailable() != null ? dto.getIsAvailable() : (initialStock > 0);

        MenuItem item = new MenuItem(
                dto.getName().trim(),
                dto.getDescription().trim(),
                dto.getPrice(),
                category,
                dto.getImageUrl() != null && !dto.getImageUrl().isEmpty() ? dto.getImageUrl().trim() : "images/menu/default.svg",
                initialStock,
                minLevel,
                available
        );

        MenuItem saved = menuRepository.save(item);

        StockMovement movement = new StockMovement(
                saved,
                StockMovement.MovementType.INITIAL,
                initialStock,
                initialStock,
                "Initial catalog stock record"
        );
        stockMovementRepository.save(movement);

        return saved;
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItemDto dto) {
        MenuItem existing = getMenuItemById(id);

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            existing.setCategory(category);
        }

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            existing.setName(dto.getName().trim());
        }
        if (dto.getDescription() != null && !dto.getDescription().trim().isEmpty()) {
            existing.setDescription(dto.getDescription().trim());
        }
        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }
        if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(dto.getImageUrl().trim());
        }
        if (dto.getMinStockLevel() != null) {
            existing.setMinStockLevel(Math.max(0, dto.getMinStockLevel()));
        }

        if (dto.getStockQuantity() != null) {
            int oldStock = existing.getStockQuantity();
            int newStock = Math.max(0, dto.getStockQuantity());
            if (oldStock != newStock) {
                existing.setStockQuantity(newStock);
                existing.setIsAvailable(newStock > 0);

                StockMovement movement = new StockMovement(
                        existing,
                        StockMovement.MovementType.ADJUSTMENT,
                        newStock - oldStock,
                        newStock,
                        "Direct inventory update via Menu Management"
                );
                stockMovementRepository.save(movement);
            }
        }

        if (dto.getIsAvailable() != null) {
            existing.setIsAvailable(dto.getIsAvailable());
        }

        return menuRepository.save(existing);
    }

    /**
     * Increases or decreases stock quantity, logs stock movement, and enforces non-negative stock constraint.
     */
    @Transactional
    public MenuItem adjustStock(Long id, StockAdjustmentRequest request) {
        MenuItem existing = getMenuItemById(id);

        if (request.getQuantityChanged() == null || request.getQuantityChanged() == 0) {
            throw new BadRequestException("Quantity change cannot be zero.");
        }

        int currentStock = existing.getStockQuantity() != null ? existing.getStockQuantity() : 0;
        int newStock = currentStock + request.getQuantityChanged();

        if (newStock < 0) {
            throw new BadRequestException("Stock cannot be negative. Current stock is " + currentStock +
                    ", attempted reduction: " + Math.abs(request.getQuantityChanged()));
        }

        existing.setStockQuantity(newStock);
        existing.setIsAvailable(newStock > 0);
        MenuItem saved = menuRepository.save(existing);

        StockMovement.MovementType mType;
        try {
            mType = StockMovement.MovementType.valueOf(request.getMovementType().toUpperCase());
        } catch (Exception e) {
            mType = request.getQuantityChanged() > 0 ? StockMovement.MovementType.MANUAL_ADD : StockMovement.MovementType.MANUAL_REDUCE;
        }

        StockMovement movement = new StockMovement(
                saved,
                mType,
                request.getQuantityChanged(),
                newStock,
                request.getReason() != null ? request.getReason() : "Manual inventory adjustment"
        );
        stockMovementRepository.save(movement);

        return saved;
    }

    public InventorySummaryDto getInventorySummary() {
        long totalProducts = menuRepository.count();
        long availableStock = menuRepository.sumAvailableStock();
        long lowStock = menuRepository.countLowStockItems();
        long outOfStock = menuRepository.countOutOfStockItems();
        BigDecimal stockValue = menuRepository.calculateTotalStockValue();

        return new InventorySummaryDto(totalProducts, availableStock, lowStock, outOfStock, stockValue);
    }

    public List<StockMovement> getStockMovementsByItem(Long itemId) {
        return stockMovementRepository.findByMenuItem_IdOrderByCreatedAtDesc(itemId);
    }

    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<MenuItem> filterMenu(Long categoryId, String stockStatus, String keyword) {
        List<MenuItem> items = menuRepository.findAll();

        return items.stream()
                .filter(item -> categoryId == null || (item.getCategory() != null && item.getCategory().getId().equals(categoryId)))
                .filter(item -> {
                    if (stockStatus == null || stockStatus.equalsIgnoreCase("ALL")) return true;
                    if (stockStatus.equalsIgnoreCase("IN_STOCK")) return item.getStockQuantity() > item.getMinStockLevel();
                    if (stockStatus.equalsIgnoreCase("LOW_STOCK")) return item.getStockQuantity() > 0 && item.getStockQuantity() <= item.getMinStockLevel();
                    if (stockStatus.equalsIgnoreCase("OUT_OF_STOCK")) return item.getStockQuantity() == 0;
                    return true;
                })
                .filter(item -> {
                    if (keyword == null || keyword.trim().isEmpty()) return true;
                    String kw = keyword.trim().toLowerCase();
                    return item.getName().toLowerCase().contains(kw) || item.getDescription().toLowerCase().contains(kw);
                })
                .collect(Collectors.toList());
    }

    public void deleteMenuItem(Long id) {
        MenuItem item = getMenuItemById(id);
        menuRepository.delete(item);
    }

    public long countAvailableItems() {
        return menuRepository.countByIsAvailableTrue();
    }
}
