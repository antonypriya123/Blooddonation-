package com.bloodconnect.controller;

import com.bloodconnect.dto.*;
import com.bloodconnect.exception.UnauthorizedException;
import com.bloodconnect.security.SecurityUtils;
import com.bloodconnect.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DonorCardDto>>> searchDonors(
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean isAvailable) {

        DonorSearchCriteria criteria = new DonorSearchCriteria(bloodGroup, city, state, isAvailable);
        List<DonorCardDto> donors = donorService.searchDonors(criteria);
        return ResponseEntity.ok(ApiResponse.success("Donors retrieved successfully", donors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DonorCardDto>> getDonorById(@PathVariable Long id) {
        DonorCardDto donor = donorService.getDonorById(id);
        return ResponseEntity.ok(ApiResponse.success("Donor details retrieved", donor));
    }

    @GetMapping("/profile/me")
    @PreAuthorize("hasRole('DONOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DonorCardDto>> getMyDonorProfile() {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));
        DonorCardDto profile = donorService.getDonorByUserId(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Donor profile retrieved", profile));
    }

    @PutMapping("/availability")
    @PreAuthorize("hasRole('DONOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAvailability(
            @RequestBody Map<String, Boolean> body) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));

        Boolean available = body.getOrDefault("isAvailable", true);
        boolean updatedStatus = donorService.updateAvailability(currentUserId, available);

        return ResponseEntity.ok(ApiResponse.success(
                "Availability status updated to " + (updatedStatus ? "AVAILABLE" : "UNAVAILABLE"),
                Map.of("isAvailable", updatedStatus)
        ));
    }

    @GetMapping("/history/me")
    @PreAuthorize("hasRole('DONOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DonationHistoryDto>>> getMyDonationHistory() {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));
        List<DonationHistoryDto> history = donorService.getDonationHistory(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Donation history retrieved", history));
    }

    @PostMapping("/history")
    @PreAuthorize("hasRole('DONOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DonationHistoryDto>> recordDonation(
            @Valid @RequestBody AddDonationDto dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));
        DonationHistoryDto history = donorService.recordDonation(currentUserId, dto);
        return ResponseEntity.ok(ApiResponse.success("Donation successfully logged in history!", history));
    }
}
