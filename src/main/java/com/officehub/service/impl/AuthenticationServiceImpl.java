package com.officehub.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.officehub.dto.AuthResponseDTO;
import com.officehub.dto.LoginRequestDTO;
import com.officehub.dto.RegisterRequestDTO;
import com.officehub.entity.User;
import com.officehub.repository.UserRepository;
import com.officehub.service.AuthenticationService;
import com.officehub.util.JwtUtil;
import com.officehub.exception.UserAlreadyExistsException;
import com.officehub.entity.Role;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;


    public AuthenticationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {
    	if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    		throw new UserAlreadyExistsException("Email already registered");
    	}
    	
    	if (request.getRole() == null ||
    		    (request.getRole() != Role.OWNER &&
    		     request.getRole() != Role.MANAGER &&
    		     request.getRole() != Role.EMPLOYEE)) {

    		    throw new IllegalArgumentException(
    		        "Invalid registration role"
    		    );
    		}
    	
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user.getRole().name()
        );

        return new AuthResponseDTO(
                token,
                "User registered successfully"
        );
    }
    
    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

    	User user = userRepository.findByEmail(request.getEmail())
    	        .orElseThrow(() -> new RuntimeException(
    	                "Account does not exist. Please create a new account."
    	        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user.getRole().name()
        );

        return new AuthResponseDTO(
                token,
                "Login successful"
        );
        
    }    
}