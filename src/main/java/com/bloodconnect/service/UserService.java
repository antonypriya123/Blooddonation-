package com.bloodconnect.service;

import com.bloodconnect.dto.UpdateProfileRequest;
import com.bloodconnect.dto.UserProfileDto;
import com.bloodconnect.entity.User;
import com.bloodconnect.enums.Role;

import java.util.List;

public interface UserService {

    User getUserById(Long id);

    User getUserByEmail(String email);

    UserProfileDto getUserProfile(Long id);

    UserProfileDto updateProfile(Long userId, UpdateProfileRequest request);

    List<UserProfileDto> getAllUsers();

    List<UserProfileDto> getUsersByRole(Role role);

    void toggleUserStatus(Long userId);

    UserProfileDto mapToDto(User user);
}
