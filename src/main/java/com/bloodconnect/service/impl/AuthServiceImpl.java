package com.bloodconnect.service.impl;

import com.bloodconnect.dto.AuthRequest;
import com.bloodconnect.dto.AuthResponse;
import com.bloodconnect.dto.RegisterRequest;
import com.bloodconnect.dto.UserProfileDto;
import com.bloodconnect.entity.DonorProfile;
import com.bloodconnect.entity.Notification;
import com.bloodconnect.entity.User;
import com.bloodconnect.enums.Role;
import com.bloodconnect.exception.BadRequestException;
import com.bloodconnect.exception.UnauthorizedException;
import com.bloodconnect.repository.DonorProfileRepository;
import com.bloodconnect.repository.NotificationRepository;
import com.bloodconnect.repository.UserRepository;
import com.bloodconnect.security.CustomUserDetails;
import com.bloodconnect.security.SecurityUtils;
import com.bloodconnect.service.AuthService;
import com.bloodconnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthServiceImpl(UserRepository userRepository,
                           DonorProfileRepository donorProfileRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           UserService userService) {
        this.userRepository = userRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Password confirmation check
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and Confirm Password do not match");
        }

        // 2. Duplicate email check
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email address already exists");
        }

        // 3. Donor-specific validation
        if (request.getRole() == Role.DONOR) {
            if (request.getBloodGroup() == null || request.getBloodGroup().trim().isEmpty()) {
                throw new BadRequestException("Blood group is required for blood donors");
            }
        }

        // 4. Create User entity
        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobileNumber(request.getMobileNumber().trim());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setCity(request.getCity().trim());
        user.setState(request.getState().trim());
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // 5. Create Donor Profile if role is DONOR
        DonorProfile savedProfile = null;
        if (request.getRole() == Role.DONOR) {
            DonorProfile profile = new DonorProfile();
            profile.setUser(savedUser);
            profile.setBloodGroup(request.getBloodGroup().trim());
            profile.setLastDonationDate(request.getLastDonationDate());
            profile.setAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
            profile.setTotalDonations(0);
            profile.setPinCode(request.getPinCode() != null ? request.getPinCode().trim() : null);
            profile.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
            savedProfile = donorProfileRepository.save(profile);
            savedUser.setDonorProfile(savedProfile);
        }

        // 6. Welcome notification
        Notification notification = new Notification(
                savedUser,
                "Welcome to BloodConnect",
                "Your " + savedUser.getRole() + " account was successfully registered. Thank you for being a lifesaver!",
                "WELCOME"
        );
        notificationRepository.save(notification);

        AuthResponse response = new AuthResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "SESSION_ACTIVE"
        );
        if (savedProfile != null) {
            response.setBloodGroup(savedProfile.getBloodGroup());
            response.setIsAvailable(savedProfile.isAvailable());
        }

        return response;
    }

    @Override
    public AuthResponse login(AuthRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String email = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());

        AuthResponse response = new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                session.getId()
        );

        if (user.getRole() == Role.DONOR && user.getDonorProfile() != null) {
            response.setBloodGroup(user.getDonorProfile().getBloodGroup());
            response.setIsAvailable(user.getDonorProfile().isAvailable());
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User is not authenticated"));
        return userService.getUserProfile(userId);
    }

    @Override
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        SecurityContextHolder.clearContext();
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
