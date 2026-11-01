package com.bloodconnect.controller;

import com.bloodconnect.dto.ApiResponse;
import com.bloodconnect.dto.DashboardStatsDto;
import com.bloodconnect.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getPublicStats() {
        DashboardStatsDto stats = dashboardService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.success("Live statistics retrieved", stats));
    }
}
