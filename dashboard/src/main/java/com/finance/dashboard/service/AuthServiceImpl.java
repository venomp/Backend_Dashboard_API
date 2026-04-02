package com.finance.dashboard.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finance.dashboard.dto.AuthResponse;
import com.finance.dashboard.dto.LoginRequest;
import com.finance.dashboard.dto.RegisterRequest;
import com.finance.dashboard.entity.UserEntity;
import com.finance.dashboard.entity.UserRole;
import com.finance.dashboard.exception.ApiException;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.security.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    public AuthServiceImpl(UserRepository userRepository, JwtService jwtService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFullName(request.getFullName());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(UserRole.VIEWER);
        user.setActive(true);

        UserEntity saved = userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setUserId(saved.getId());
        response.setMessage("User registered successfully");

        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid credentials"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new ApiException("User is inactive");
        }

        String token = jwtService.generateToken(user.getId());

        AuthResponse response = new AuthResponse();
        response.setMessage("Login successful");
        response.setToken(token);
        response.setType("Bearer");
        response.setUserId(user.getId());

        return response;
    }
}

