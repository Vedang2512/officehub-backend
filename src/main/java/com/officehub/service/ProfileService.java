package com.officehub.service;

import com.officehub.dto.profile.ChangePasswordRequest;
import com.officehub.dto.profile.NotificationPreferencesDTO;
import com.officehub.dto.profile.ProfileResponseDTO;
import com.officehub.dto.profile.UpdateProfileRequest;

public interface ProfileService {

    ProfileResponseDTO getProfile(String email);

    ProfileResponseDTO updateProfile(
            String email,
            UpdateProfileRequest request
    );

    void changePassword(
            String email,
            ChangePasswordRequest request
    );

    String updateProfileImage(
            String email,
            String imageUrl
    );

    NotificationPreferencesDTO getNotificationPreferences(
            String email
    );

    NotificationPreferencesDTO updateNotificationPreferences(
            String email,
            NotificationPreferencesDTO request
    );

}