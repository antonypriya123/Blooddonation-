package com.bloodconnect.controller;

import com.bloodconnect.dto.*;
import com.bloodconnect.service.BloodRequestService;
import com.bloodconnect.service.DashboardService;
import com.bloodconnect.service.DonorService;
import com.bloodconnect.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final DonorService donorService;
    private final BloodRequestService bloodRequestService;
    private final DashboardService dashboardService;

    public AdminController(UserService userService,
                           DonorService donorService,
                           BloodRequestService bloodRequestService,
                           DashboardService dashboardService) {
        this.userService = userService;
        this.donorService = donorService;
        this.bloodRequestService = bloodRequestService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getAdminStats() {
        DashboardStatsDto stats = dashboardService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.success("Admin statistics retrieved", stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> getAllUsers() {
        List<UserProfileDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved", users));
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.success("User active status updated successfully"));
    }

    @GetMapping("/donors")
    public ResponseEntity<ApiResponse<List<DonorCardDto>>> getAllDonors() {
        List<DonorCardDto> donors = donorService.getAllDonors();
        return ResponseEntity.ok(ApiResponse.success("All donors retrieved", donors));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<BloodRequestDto>>> getAllRequests() {
        List<BloodRequestDto> requests = bloodRequestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success("All blood requests retrieved", requests));
    }
}
