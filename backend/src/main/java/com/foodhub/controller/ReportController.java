package com.foodhub.controller;

import com.foodhub.dto.ReportSummaryDto;
import com.foodhub.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ReportController
 * Exposes statistical sales analysis metrics for Administrator Dashboards.
 */
@RestController
@RequestMapping("/api/admin/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryDto> getSummary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    @GetMapping("/daily")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue() {
        return ResponseEntity.ok(reportService.getDailyRevenue());
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue() {
        return ResponseEntity.ok(reportService.getMonthlyRevenue());
    }

    @GetMapping("/popular-items")
    public ResponseEntity<List<Map<String, Object>>> getPopularItems() {
        return ResponseEntity.ok(reportService.getPopularFoodItems());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategoryRevenue() {
        return ResponseEntity.ok(reportService.getCategoryRevenue());
    }
}
