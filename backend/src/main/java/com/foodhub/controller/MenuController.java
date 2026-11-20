package com.foodhub.controller;

import com.foodhub.dto.ApiResponse;
import com.foodhub.dto.InventorySummaryDto;
import com.foodhub.dto.MenuItemDto;
import com.foodhub.dto.StockAdjustmentRequest;
import com.foodhub.entity.MenuItem;
import com.foodhub.entity.StockMovement;
import com.foodhub.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MenuController
 * Exposes REST endpoints for food item catalog, searching, sorting,
 * and comprehensive inventory and stock management.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/menu")
    public ResponseEntity<List<MenuItem>> getAllMenu(@RequestParam(required = false, defaultValue = "false") boolean all) {
        if (all) {
            return ResponseEntity.ok(menuService.getAllMenuItems());
        }
        return ResponseEntity.ok(menuService.getAvailableMenuItems());
    }

    @GetMapping("/menu/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }

    @GetMapping("/menu/category/{categoryId}")
    public ResponseEntity<List<MenuItem>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(menuService.getItemsByCategory(categoryId));
    }

    @GetMapping("/menu/search")
    public ResponseEntity<List<MenuItem>> searchMenu(@RequestParam(name = "q", required = false) String keyword) {
        return ResponseEntity.ok(menuService.searchMenuItems(keyword));
    }

    @GetMapping("/menu/sort")
    public ResponseEntity<List<MenuItem>> getSortedByPrice(@RequestParam(name = "asc", defaultValue = "true") boolean ascending) {
        return ResponseEntity.ok(menuService.getItemsSortedByPrice(ascending));
    }

    @PostMapping("/menu")
    public ResponseEntity<MenuItem> createMenuItem(@Valid @RequestBody MenuItemDto dto) {
        MenuItem created = menuService.createMenuItem(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/menu/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemDto dto) {
        MenuItem updated = menuService.updateMenuItem(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/menu/{id}")
    public ResponseEntity<ApiResponse> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(new ApiResponse(true, "Menu item deleted successfully."));
    }

    // ----------------------------------------------------------------
    // Stock and Inventory Management Endpoints
    // ----------------------------------------------------------------

    @GetMapping("/admin/inventory/summary")
    public ResponseEntity<InventorySummaryDto> getInventorySummary() {
        return ResponseEntity.ok(menuService.getInventorySummary());
    }

    @GetMapping("/admin/inventory/filter")
    public ResponseEntity<List<MenuItem>> filterInventory(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String stockStatus,
            @RequestParam(required = false) String search) {
        List<MenuItem> items = menuService.filterMenu(categoryId, stockStatus, search);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/admin/inventory/{id}/adjust")
    public ResponseEntity<MenuItem> adjustStock(
            @PathVariable Long id,
            @Valid @RequestBody StockAdjustmentRequest request) {
        MenuItem updated = menuService.adjustStock(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/admin/inventory/{id}/movements")
    public ResponseEntity<List<StockMovement>> getItemStockMovements(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getStockMovementsByItem(id));
    }

    @GetMapping("/admin/inventory/movements")
    public ResponseEntity<List<StockMovement>> getAllStockMovements() {
        return ResponseEntity.ok(menuService.getAllStockMovements());
    }
}
