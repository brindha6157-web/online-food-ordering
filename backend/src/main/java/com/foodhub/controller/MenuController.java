package com.foodhub.controller;

import com.foodhub.dto.ApiResponse;
import com.foodhub.dto.MenuItemDto;
import com.foodhub.entity.MenuItem;
import com.foodhub.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MenuController
 * Exposes REST endpoints for food item browsing, searching, and administration.
 */
@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenu(@RequestParam(required = false, defaultValue = "false") boolean all) {
        if (all) {
            return ResponseEntity.ok(menuService.getAllMenuItems());
        }
        return ResponseEntity.ok(menuService.getAvailableMenuItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuItem>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(menuService.getItemsByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchMenu(@RequestParam(name = "q", required = false) String keyword) {
        return ResponseEntity.ok(menuService.searchMenuItems(keyword));
    }

    @GetMapping("/sort")
    public ResponseEntity<List<MenuItem>> getSortedByPrice(@RequestParam(name = "asc", defaultValue = "true") boolean ascending) {
        return ResponseEntity.ok(menuService.getItemsSortedByPrice(ascending));
    }

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@Valid @RequestBody MenuItemDto dto) {
        MenuItem created = menuService.createMenuItem(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemDto dto) {
        MenuItem updated = menuService.updateMenuItem(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(new ApiResponse(true, "Menu item deleted successfully."));
    }
}
