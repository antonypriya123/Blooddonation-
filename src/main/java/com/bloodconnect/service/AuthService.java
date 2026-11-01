package com.bloodconnect.service;

import com.bloodconnect.dto.AuthRequest;
import com.bloodconnect.dto.AuthResponse;
import com.bloodconnect.dto.RegisterRequest;
import com.bloodconnect.dto.UserProfileDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    UserProfileDto getCurrentUser();

    void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse);
}
