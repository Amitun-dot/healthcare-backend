package com.healthcare.service;

import com.healthcare.dto.AuthResponse;
import com.healthcare.dto.LoginRequest;
import com.healthcare.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}