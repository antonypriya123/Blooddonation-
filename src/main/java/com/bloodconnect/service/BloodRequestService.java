package com.bloodconnect.service;

import com.bloodconnect.dto.BloodRequestDto;
import com.bloodconnect.dto.CreateBloodRequestDto;
import com.bloodconnect.dto.UpdateStatusDto;
import com.bloodconnect.enums.RequestStatus;
import com.bloodconnect.enums.UrgencyLevel;

import java.util.List;

public interface BloodRequestService {

    BloodRequestDto createRequest(Long requesterId, CreateBloodRequestDto dto);

    BloodRequestDto getRequestById(Long id);

    List<BloodRequestDto> getRequestsByRequester(Long requesterId);

    List<BloodRequestDto> getAllRequests();

    List<BloodRequestDto> getRecentEmergencyRequests();

    List<BloodRequestDto> searchRequests(RequestStatus status, String bloodGroup, String city, UrgencyLevel urgency);

    BloodRequestDto updateStatus(Long requestId, Long currentUserId, UpdateStatusDto dto);

    BloodRequestDto cancelRequest(Long requestId, Long currentUserId);
}
