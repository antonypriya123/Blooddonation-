package com.bloodconnect.service;

import com.bloodconnect.dto.*;
import com.bloodconnect.entity.DonorProfile;

import java.util.List;

public interface DonorService {

    List<DonorCardDto> searchDonors(DonorSearchCriteria criteria);

    DonorCardDto getDonorById(Long donorId);

    DonorCardDto getDonorByUserId(Long userId);

    DonorProfile getDonorProfileByUserId(Long userId);

    boolean updateAvailability(Long userId, boolean isAvailable);

    DonationHistoryDto recordDonation(Long userId, AddDonationDto dto);

    List<DonationHistoryDto> getDonationHistory(Long userId);

    List<DonorCardDto> getAllDonors();

    DonorCardDto mapToCardDto(DonorProfile profile);
}
