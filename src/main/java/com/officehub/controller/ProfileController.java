package com.officehub.controller;

import com.officehub.dto.profile.ChangePasswordRequest;
import com.officehub.dto.profile.NotificationPreferencesDTO;
import com.officehub.dto.profile.ProfileResponseDTO;
import com.officehub.dto.profile.UpdateProfileRequest;
import com.officehub.service.ProfileService;
import com.officehub.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.officehub.service.CloudinaryService;
@RestController
@RequestMapping("/api/users/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtUtil jwtUtil;
    private final CloudinaryService cloudinaryService;

    public ProfileController(ProfileService profileService,
	            JwtUtil jwtUtil,
	            CloudinaryService cloudinaryService) {
	
	this.profileService = profileService;
	this.jwtUtil = jwtUtil;
	this.cloudinaryService = cloudinaryService;
	}

    private String getEmail(HttpServletRequest request) {

        String token = request.getHeader("Authorization")
                .replace("Bearer ", "");

        return jwtUtil.extractUsername(token);
    }

    @GetMapping
    public ResponseEntity<ProfileResponseDTO> getProfile(
            HttpServletRequest request) {

        return ResponseEntity.ok(
                profileService.getProfile(getEmail(request))
        );
    }

    @PutMapping
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @Valid @RequestBody UpdateProfileRequest requestDto,
            HttpServletRequest request) {

        return ResponseEntity.ok(
                profileService.updateProfile(
                        getEmail(request),
                        requestDto
                )
        );
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest requestDto,
            HttpServletRequest request) {

        profileService.changePassword(
                getEmail(request),
                requestDto
        );

        return ResponseEntity.ok("Password updated successfully");
    }
    
    @GetMapping("/notifications")
    public ResponseEntity<NotificationPreferencesDTO> getNotificationPreferences(
            HttpServletRequest request) {

        return ResponseEntity.ok(
                profileService.getNotificationPreferences(
                        getEmail(request)
                )
        );
    }

    @PutMapping("/notifications")
    public ResponseEntity<NotificationPreferencesDTO> updateNotificationPreferences(
            @RequestBody NotificationPreferencesDTO requestDto,
            HttpServletRequest request) {

        return ResponseEntity.ok(
                profileService.updateNotificationPreferences(
                        getEmail(request),
                        requestDto
                )
        );
    }
    
    @PostMapping(
            value = "/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {


        String imageUrl =
                cloudinaryService.uploadImage(file);


        String updatedUrl =
                profileService.updateProfileImage(
                        getEmail(request),
                        imageUrl
                );


        return ResponseEntity.ok(updatedUrl);
    }

}