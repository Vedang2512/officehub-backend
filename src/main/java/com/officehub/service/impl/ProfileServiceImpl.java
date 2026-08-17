package com.officehub.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.officehub.dto.profile.NotificationPreferencesDTO;
import org.springframework.stereotype.Service;

import com.officehub.dto.profile.ChangePasswordRequest;
import com.officehub.dto.profile.ProfileResponseDTO;
import com.officehub.dto.profile.UpdateProfileRequest;
import com.officehub.entity.User;
import com.officehub.repository.UserRepository;
import com.officehub.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileServiceImpl(UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ProfileResponseDTO getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ProfileResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .organizationName(
                        user.getOrganization() != null
                                ? user.getOrganization().getOrganizationName()
                                : null
                )
                .phoneNumber(user.getPhoneNumber())
                .designation(user.getDesignation())
                .profileImage(user.getProfileImage())
                .build();
    }

    @Override
    public ProfileResponseDTO updateProfile(String email,
                                            UpdateProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDesignation(request.getDesignation());

        userRepository.save(user);

        return getProfile(email);
    }

    @Override
    public void changePassword(String email,
                               ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }

    @Override
    public String updateProfileImage(String email,
                                     String imageUrl) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfileImage(imageUrl);

        userRepository.save(user);

        return imageUrl;
    }
    
    @Override
    public NotificationPreferencesDTO getNotificationPreferences(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new NotificationPreferencesDTO(
                user.isEmailNotifications(),
                user.isTaskNotifications(),
                user.isChatNotifications()
        );
    }

    @Override
    public NotificationPreferencesDTO updateNotificationPreferences(
            String email,
            NotificationPreferencesDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailNotifications(request.isEmailNotifications());
        user.setTaskNotifications(request.isTaskNotifications());
        user.setChatNotifications(request.isChatNotifications());

        userRepository.save(user);

        return new NotificationPreferencesDTO(
                user.isEmailNotifications(),
                user.isTaskNotifications(),
                user.isChatNotifications()
        );
    }
}