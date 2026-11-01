package com.bloodconnect.service.impl;

import com.bloodconnect.dto.DashboardStatsDto;
import com.bloodconnect.enums.RequestStatus;
import com.bloodconnect.enums.Role;
import com.bloodconnect.repository.BloodRequestRepository;
import com.bloodconnect.repository.DonorProfileRepository;
import com.bloodconnect.repository.UserRepository;
import com.bloodconnect.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final BloodRequestRepository bloodRequestRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                DonorProfileRepository donorProfileRepository,
                                BloodRequestRepository bloodRequestRepository) {
        this.userRepository = userRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.bloodRequestRepository = bloodRequestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getSystemStatistics() {
        long totalUsers = userRepository.count();
        long totalDonors = userRepository.countByRole(Role.DONOR);
        long totalRequesters = userRepository.countByRole(Role.REQUESTER);
        long totalRequests = bloodRequestRepository.count();
        long pendingRequests = bloodRequestRepository.countByStatus(RequestStatus.PENDING);
        long fulfilledRequests = bloodRequestRepository.countByStatus(RequestStatus.FULFILLED);
        long acceptedRequests = bloodRequestRepository.countByStatus(RequestStatus.ACCEPTED);
        long cancelledRequests = bloodRequestRepository.countByStatus(RequestStatus.CANCELLED);
        long availableDonors = donorProfileRepository.countByIsAvailableTrue();

        return new DashboardStatsDto(
                totalUsers,
                totalDonors,
                totalRequesters,
                totalRequests,
                pendingRequests,
                fulfilledRequests,
                acceptedRequests,
                cancelledRequests,
                availableDonors
        );
    }
}
