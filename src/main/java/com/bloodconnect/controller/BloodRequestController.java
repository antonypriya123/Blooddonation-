package com.bloodconnect.controller;

import com.bloodconnect.dto.ApiResponse;
import com.bloodconnect.dto.BloodRequestDto;
import com.bloodconnect.dto.CreateBloodRequestDto;
import com.bloodconnect.dto.UpdateStatusDto;
import com.bloodconnect.enums.RequestStatus;
import com.bloodconnect.enums.UrgencyLevel;
import com.bloodconnect.exception.UnauthorizedException;
import com.bloodconnect.security.SecurityUtils;
import com.bloodconnect.service.BloodRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;

    public BloodRequestController(BloodRequestService bloodRequestService) {
        this.bloodRequestService = bloodRequestService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BloodRequestDto>> createRequest(
            @Valid @RequestBody CreateBloodRequestDto dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));
        BloodRequestDto created = bloodRequestService.createRequest(currentUserId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Blood request submitted successfully and broadcasted!", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BloodRequestDto>>> searchRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) UrgencyLevel urgency) {
        List<BloodRequestDto> requests = bloodRequestService.searchRequests(status, bloodGroup, city, urgency);
        return ResponseEntity.ok(ApiResponse.success("Blood requests retrieved", requests));
    }

    @GetMapping("/emergency")
    public ResponseEntity<ApiResponse<List<BloodRequestDto>>> getRecentEmergencyRequests() {
        List<BloodRequestDto> requests = bloodRequestService.getRecentEmergencyRequests();
        return ResponseEntity.ok(ApiResponse.success("Emergency requests retrieved", requests));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<BloodRequestDto>>> getMyRequests() {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));
        List<BloodRequestDto> myRequests = bloodRequestService.getRequestsByRequester(currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Your blood requests retrieved", myRequests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BloodRequestDto>> getRequestById(@PathVariable Long id) {
        BloodRequestDto request = bloodRequestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success("Blood request details retrieved", request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BloodRequestDto>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusDto dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));
        BloodRequestDto updated = bloodRequestService.updateStatus(id, currentUserId, dto);
        return ResponseEntity.ok(ApiResponse.success("Request status successfully updated to " + updated.getStatus(), updated));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BloodRequestDto>> cancelRequest(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));
        BloodRequestDto cancelled = bloodRequestService.cancelRequest(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Blood request cancelled successfully", cancelled));
    }
}
