package com.officehub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.officehub.dto.AuthResponseDTO;
import com.officehub.dto.LoginRequestDTO;
import com.officehub.dto.RegisterRequestDTO;
import com.officehub.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {

        AuthResponseDTO response = authenticationService.register(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {

        AuthResponseDTO response = authenticationService.login(request);

        return ResponseEntity.ok(response);
    }
}