package com.bloodconnect.service.impl;

import com.bloodconnect.dto.UpdateProfileRequest;
import com.bloodconnect.dto.UserProfileDto;
import com.bloodconnect.entity.DonorProfile;
import com.bloodconnect.entity.User;
import com.bloodconnect.enums.Role;
import com.bloodconnect.exception.ResourceNotFoundException;
import com.bloodconnect.repository.DonorProfileRepository;
import com.bloodconnect.repository.UserRepository;
import com.bloodconnect.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;

    public UserServiceImpl(UserRepository userRepository, DonorProfileRepository donorProfileRepository) {
        this.userRepository = userRepository;
        this.donorProfileRepository = donorProfileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long id) {
        User user = getUserById(id);
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserProfileDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getUserById(userId);

        user.setFullName(request.getFullName().trim());
        user.setMobileNumber(request.getMobileNumber().trim());
        user.setCity(request.getCity().trim());
        user.setState(request.getState().trim());

        if (user.getRole() == Role.DONOR) {
            DonorProfile profile = donorProfileRepository.findByUser(user)
                    .orElseGet(() -> {
                        DonorProfile newProfile = new DonorProfile();
                        newProfile.setUser(user);
                        return newProfile;
                    });

            if (request.getBloodGroup() != null && !request.getBloodGroup().trim().isEmpty()) {
                profile.setBloodGroup(request.getBloodGroup().trim());
            }
            if (request.getLastDonationDate() != null) {
                profile.setLastDonationDate(request.getLastDonationDate());
            }
            if (request.getIsAvailable() != null) {
                profile.setAvailable(request.getIsAvailable());
            }
            if (request.getPinCode() != null) {
                profile.setPinCode(request.getPinCode().trim());
            }
            if (request.getAddress() != null) {
                profile.setAddress(request.getAddress().trim());
            }
            donorProfileRepository.save(profile);
            user.setDonorProfile(profile);
        }

        userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileDto> getUsersByRole(Role role) {
        return userRepository.findByRoleOrderByCreatedAtDesc(role).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    public UserProfileDto mapToDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender());
        dto.setCity(user.getCity());
        dto.setState(user.getState());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getRole() == Role.DONOR) {
            DonorProfile profile = donorProfileRepository.findByUser(user).orElse(null);
            if (profile != null) {
                dto.setDonorProfileId(profile.getId());
                dto.setBloodGroup(profile.getBloodGroup());
                dto.setLastDonationDate(profile.getLastDonationDate());
                dto.setIsAvailable(profile.isAvailable());
                dto.setTotalDonations(profile.getTotalDonations());
                dto.setPinCode(profile.getPinCode());
                dto.setAddress(profile.getAddress());
            }
        }

        return dto;
    }
}
