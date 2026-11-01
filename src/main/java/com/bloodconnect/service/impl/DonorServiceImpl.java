package com.bloodconnect.service.impl;

import com.bloodconnect.dto.*;
import com.bloodconnect.entity.BloodRequest;
import com.bloodconnect.entity.DonationHistory;
import com.bloodconnect.entity.DonorProfile;
import com.bloodconnect.entity.User;
import com.bloodconnect.exception.ResourceNotFoundException;
import com.bloodconnect.repository.BloodRequestRepository;
import com.bloodconnect.repository.DonationHistoryRepository;
import com.bloodconnect.repository.DonorProfileRepository;
import com.bloodconnect.repository.UserRepository;
import com.bloodconnect.service.DonorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonorServiceImpl implements DonorService {

    private final DonorProfileRepository donorProfileRepository;
    private final UserRepository userRepository;
    private final DonationHistoryRepository donationHistoryRepository;
    private final BloodRequestRepository bloodRequestRepository;

    public DonorServiceImpl(DonorProfileRepository donorProfileRepository,
                            UserRepository userRepository,
                            DonationHistoryRepository donationHistoryRepository,
                            BloodRequestRepository bloodRequestRepository) {
        this.donorProfileRepository = donorProfileRepository;
        this.userRepository = userRepository;
        this.donationHistoryRepository = donationHistoryRepository;
        this.bloodRequestRepository = bloodRequestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonorCardDto> searchDonors(DonorSearchCriteria criteria) {
        String bg = (criteria.getBloodGroup() != null && !criteria.getBloodGroup().trim().isEmpty())
                ? criteria.getBloodGroup().trim() : null;
        String city = (criteria.getCity() != null && !criteria.getCity().trim().isEmpty())
                ? criteria.getCity().trim() : null;
        String state = (criteria.getState() != null && !criteria.getState().trim().isEmpty())
                ? criteria.getState().trim() : null;

        List<DonorProfile> profiles = donorProfileRepository.searchDonors(bg, city, state, criteria.getIsAvailable());
        return profiles.stream()
                .map(this::mapToCardDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DonorCardDto getDonorById(Long donorId) {
        DonorProfile profile = donorProfileRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found with id: " + donorId));
        return mapToCardDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public DonorCardDto getDonorByUserId(Long userId) {
        DonorProfile profile = getDonorProfileByUserId(userId);
        return mapToCardDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public DonorProfile getDonorProfileByUserId(Long userId) {
        return donorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found for user id: " + userId));
    }

    @Override
    @Transactional
    public boolean updateAvailability(Long userId, boolean isAvailable) {
        DonorProfile profile = getDonorProfileByUserId(userId);
        profile.setAvailable(isAvailable);
        donorProfileRepository.save(profile);
        return profile.isAvailable();
    }

    @Override
    @Transactional
    public DonationHistoryDto recordDonation(Long userId, AddDonationDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        DonorProfile profile = getDonorProfileByUserId(userId);

        BloodRequest request = null;
        if (dto.getBloodRequestId() != null) {
            request = bloodRequestRepository.findById(dto.getBloodRequestId()).orElse(null);
        }

        DonationHistory history = new DonationHistory();
        history.setDonor(user);
        history.setBloodRequest(request);
        history.setDonationDate(dto.getDonationDate());
        history.setUnitsDonated(dto.getUnitsDonated() != null ? dto.getUnitsDonated() : 1);
        history.setHospitalName(dto.getHospitalName().trim());
        history.setRemarks(dto.getRemarks() != null ? dto.getRemarks().trim() : null);

        DonationHistory saved = donationHistoryRepository.save(history);

        // Update profile latest donation date and counter
        if (profile.getLastDonationDate() == null || dto.getDonationDate().isAfter(profile.getLastDonationDate())) {
            profile.setLastDonationDate(dto.getDonationDate());
        }
        profile.setTotalDonations(profile.getTotalDonations() + saved.getUnitsDonated());
        donorProfileRepository.save(profile);

        return new DonationHistoryDto(
                saved.getId(),
                user.getId(),
                user.getFullName(),
                request != null ? request.getId() : null,
                saved.getDonationDate(),
                saved.getUnitsDonated(),
                saved.getHospitalName(),
                saved.getRemarks(),
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationHistoryDto> getDonationHistory(Long userId) {
        return donationHistoryRepository.findByDonorIdOrderByDonationDateDesc(userId).stream()
                .map(dh -> new DonationHistoryDto(
                        dh.getId(),
                        dh.getDonor().getId(),
                        dh.getDonor().getFullName(),
                        dh.getBloodRequest() != null ? dh.getBloodRequest().getId() : null,
                        dh.getDonationDate(),
                        dh.getUnitsDonated(),
                        dh.getHospitalName(),
                        dh.getRemarks(),
                        dh.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonorCardDto> getAllDonors() {
        return donorProfileRepository.findAll().stream()
                .map(this::mapToCardDto)
                .collect(Collectors.toList());
    }

    @Override
    public DonorCardDto mapToCardDto(DonorProfile profile) {
        User u = profile.getUser();
        return new DonorCardDto(
                profile.getId(),
                u.getId(),
                u.getFullName(),
                profile.getBloodGroup(),
                u.getCity(),
                u.getState(),
                profile.isAvailable(),
                profile.getLastDonationDate(),
                profile.getTotalDonations(),
                u.getEmail(),
                u.getMobileNumber(),
                profile.getAddress()
        );
    }
}
