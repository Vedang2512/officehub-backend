package com.officehub.service;

import com.officehub.dto.AuthResponseDTO;
import com.officehub.dto.LoginRequestDTO;
import com.officehub.dto.RegisterRequestDTO;

public interface AuthenticationService {

	AuthResponseDTO register(RegisterRequestDTO request);

	AuthResponseDTO login(LoginRequestDTO request);

}