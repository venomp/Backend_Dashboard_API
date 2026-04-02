package com.finance.dashboard.service;

import com.finance.dashboard.dto.AuthResponse;
import com.finance.dashboard.dto.LoginRequest;
import com.finance.dashboard.dto.RegisterRequest;

public interface AuthService {


    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    
} 
