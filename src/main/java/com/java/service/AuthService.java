package com.java.service;

import com.java.dto.request.LoginRequest;
import com.java.dto.request.RegisteRequest;
import com.java.dto.response.JwtAuthResponse;

public interface AuthService {
	JwtAuthResponse register(RegisteRequest registerDto);

	JwtAuthResponse login(LoginRequest loginDto);
}
