package com.officehub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.officehub.dto.UserProfileDTO;
import com.officehub.entity.User;
import com.officehub.repository.UserRepository;
import com.officehub.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserRepository userRepository;
	private final UserService userService;


	public UserController(
	        UserRepository userRepository,
	        UserService userService
	) {

	    this.userRepository = userRepository;
	    this.userService = userService;
	}


    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(
            Authentication authentication) {


        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                    new RuntimeException("User not found")
                );


        UserProfileDTO response = new UserProfileDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getProfileImage(),
                user.getOrganization() != null
                        ? user.getOrganization().getId()
                        : null
        );


        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(
            Authentication authentication
    ) {

        userService.deleteCurrentUser(
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Account deleted successfully"
        );
    }
    
    @PutMapping("/{userId}/manager")
    public ResponseEntity<String> assignManager(
            @PathVariable Long userId,
            Authentication authentication) {

        userService.assignManager(
                userId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "User assigned as manager successfully."
        );
    }
    
}