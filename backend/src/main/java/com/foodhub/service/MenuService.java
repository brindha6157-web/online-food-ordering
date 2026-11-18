package com.foodhub.service;

import com.foodhub.dto.MenuItemDto;
import com.foodhub.entity.Category;
import com.foodhub.entity.MenuItem;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.repository.CategoryRepository;
import com.foodhub.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MenuService
 * Handles food catalog operations.
 * Demonstrates: Java Stream API (filter, map, sorted, collect), Reusable business logic.
 */
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository, CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuRepository.findAll();
    }

    public List<MenuItem> getAvailableMenuItems() {
        // Demonstrating Stream API filter
        return menuRepository.findAll().stream()
                .filter(MenuItem::getIsAvailable)
                .collect(Collectors.toList());
    }

    public List<MenuItem> getItemsByCategory(Long categoryId) {
        return menuRepository.findByCategoryId(categoryId).stream()
                .filter(MenuItem::getIsAvailable)
                .collect(Collectors.toList());
    }

    public List<MenuItem> searchMenuItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAvailableMenuItems();
        }
        String cleanKeyword = keyword.trim().toLowerCase();
        // Demonstrating Stream API filter with String contains check
        return menuRepository.findAll().stream()
                .filter(MenuItem::getIsAvailable)
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
                .filter(MenuItem::getIsAvailable)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public MenuItem getMenuItemById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    public MenuItem createMenuItem(MenuItemDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        MenuItem item = new MenuItem(
                dto.getName().trim(),
                dto.getDescription().trim(),
                dto.getPrice(),
                category,
                dto.getImageUrl() != null && !dto.getImageUrl().isEmpty() ? dto.getImageUrl().trim() : "/images/menu/default.jpg",
                dto.getIsAvailable() != null ? dto.getIsAvailable() : true
        );

        return menuRepository.save(item);
    }

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
        if (dto.getIsAvailable() != null) {
            existing.setIsAvailable(dto.getIsAvailable());
        }

        return menuRepository.save(existing);
    }

    public void deleteMenuItem(Long id) {
        MenuItem item = getMenuItemById(id);
        menuRepository.delete(item);
    }

    public long countAvailableItems() {
        return menuRepository.countByIsAvailableTrue();
    }
}
