package com.bloodconnect.service.impl;

import com.bloodconnect.dto.BloodRequestDto;
import com.bloodconnect.dto.CreateBloodRequestDto;
import com.bloodconnect.dto.UpdateStatusDto;
import com.bloodconnect.entity.BloodRequest;
import com.bloodconnect.entity.Notification;
import com.bloodconnect.entity.User;
import com.bloodconnect.enums.RequestStatus;
import com.bloodconnect.enums.Role;
import com.bloodconnect.enums.UrgencyLevel;
import com.bloodconnect.exception.BadRequestException;
import com.bloodconnect.exception.ResourceNotFoundException;
import com.bloodconnect.exception.UnauthorizedException;
import com.bloodconnect.repository.BloodRequestRepository;
import com.bloodconnect.repository.NotificationRepository;
import com.bloodconnect.repository.UserRepository;
import com.bloodconnect.service.BloodRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodRequestServiceImpl implements BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public BloodRequestServiceImpl(BloodRequestRepository bloodRequestRepository,
                                   UserRepository userRepository,
                                   NotificationRepository notificationRepository) {
        this.bloodRequestRepository = bloodRequestRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public BloodRequestDto createRequest(Long requesterId, CreateBloodRequestDto dto) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requesterId));

        BloodRequest request = new BloodRequest();
        request.setRequester(requester);
        request.setPatientName(dto.getPatientName().trim());
        request.setBloodGroup(dto.getBloodGroup().trim());
        request.setUnitsRequired(dto.getUnitsRequired());
        request.setHospitalName(dto.getHospitalName().trim());
        request.setHospitalAddress(dto.getHospitalAddress().trim());
        request.setCity(dto.getCity().trim());
        request.setState(dto.getState().trim());
        request.setContactNumber(dto.getContactNumber().trim());
        request.setRequiredDate(dto.getRequiredDate());
        request.setUrgencyLevel(dto.getUrgencyLevel() != null ? dto.getUrgencyLevel() : UrgencyLevel.MEDIUM);
        request.setStatus(RequestStatus.PENDING);
        request.setAdditionalInfo(dto.getAdditionalInfo() != null ? dto.getAdditionalInfo().trim() : null);

        BloodRequest saved = bloodRequestRepository.save(request);

        // Notify requester
        Notification notification = new Notification(
                requester,
                "Blood Request Broadcasted",
                "Your request for " + saved.getUnitsRequired() + " unit(s) of " + saved.getBloodGroup() +
                        " for patient " + saved.getPatientName() + " has been broadcasted.",
                "REQUEST"
        );
        notificationRepository.save(notification);

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BloodRequestDto getRequestById(Long id) {
        BloodRequest request = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found with id: " + id));
        return mapToDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodRequestDto> getRequestsByRequester(Long requesterId) {
        return bloodRequestRepository.findByRequesterIdOrderByCreatedAtDesc(requesterId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodRequestDto> getAllRequests() {
        return bloodRequestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodRequestDto> getRecentEmergencyRequests() {
        return bloodRequestRepository.findTop6ByStatusOrderByCreatedAtDesc(RequestStatus.PENDING).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodRequestDto> searchRequests(RequestStatus status, String bloodGroup, String city, UrgencyLevel urgency) {
        String bg = (bloodGroup != null && !bloodGroup.trim().isEmpty()) ? bloodGroup.trim() : null;
        String c = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        return bloodRequestRepository.searchRequests(status, bg, c, urgency).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BloodRequestDto updateStatus(Long requestId, Long currentUserId, UpdateStatusDto dto) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found: " + requestId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        // Authorization check: Admin, or the requester themselves, or an assigned donor
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = request.getRequester().getId().equals(currentUser.getId());
        boolean isDonor = currentUser.getRole() == Role.DONOR;

        if (!isAdmin && !isOwner && !isDonor) {
            throw new UnauthorizedException("You do not have permission to update this request status");
        }

        // Donor can accept request
        if (isDonor && !isAdmin && !isOwner) {
            if (dto.getStatus() == RequestStatus.ACCEPTED && request.getStatus() == RequestStatus.PENDING) {
                request.setStatus(RequestStatus.ACCEPTED);
                request.setAssignedDonor(currentUser);
            } else {
                throw new BadRequestException("Donors can only accept pending requests");
            }
        } else {
            request.setStatus(dto.getStatus());
            if (dto.getAssignedDonorId() != null) {
                User donor = userRepository.findById(dto.getAssignedDonorId()).orElse(null);
                request.setAssignedDonor(donor);
            }
        }

        BloodRequest updated = bloodRequestRepository.save(request);

        // Notify requester about status change
        Notification notification = new Notification(
                updated.getRequester(),
                "Request Status Updated: " + updated.getStatus(),
                "Your blood request for " + updated.getPatientName() + " (" + updated.getBloodGroup() +
                        ") is now " + updated.getStatus() + ".",
                "STATUS_CHANGE"
        );
        notificationRepository.save(notification);

        return mapToDto(updated);
    }

    @Override
    @Transactional
    public BloodRequestDto cancelRequest(Long requestId, Long currentUserId) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found: " + requestId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = request.getRequester().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("You are not authorized to cancel this request");
        }

        if (request.getStatus() == RequestStatus.FULFILLED) {
            throw new BadRequestException("Fulfilled requests cannot be cancelled");
        }

        request.setStatus(RequestStatus.CANCELLED);
        BloodRequest updated = bloodRequestRepository.save(request);
        return mapToDto(updated);
    }

    private BloodRequestDto mapToDto(BloodRequest r) {
        BloodRequestDto dto = new BloodRequestDto();
        dto.setId(r.getId());
        dto.setRequesterId(r.getRequester().getId());
        dto.setRequesterName(r.getRequester().getFullName());
        dto.setRequesterEmail(r.getRequester().getEmail());
        dto.setPatientName(r.getPatientName());
        dto.setBloodGroup(r.getBloodGroup());
        dto.setUnitsRequired(r.getUnitsRequired());
        dto.setHospitalName(r.getHospitalName());
        dto.setHospitalAddress(r.getHospitalAddress());
        dto.setCity(r.getCity());
        dto.setState(r.getState());
        dto.setContactNumber(r.getContactNumber());
        dto.setRequiredDate(r.getRequiredDate());
        dto.setUrgencyLevel(r.getUrgencyLevel());
        dto.setStatus(r.getStatus());
        dto.setAdditionalInfo(r.getAdditionalInfo());
        if (r.getAssignedDonor() != null) {
            dto.setAssignedDonorId(r.getAssignedDonor().getId());
            dto.setAssignedDonorName(r.getAssignedDonor().getFullName());
        }
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }
}
