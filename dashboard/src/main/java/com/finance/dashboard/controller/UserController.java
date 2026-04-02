package com.finance.dashboard.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.dashboard.dto.RoleUpdateRequest;
import com.finance.dashboard.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users") 
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/role")
    public ResponseEntity<String> updateRole(@Valid @RequestBody RoleUpdateRequest request) {
        userService.updateRole(request.getUserId(), request.getNewRole());
        return ResponseEntity.ok("Role updated successfully for user ID: " + request.getUserId());
    }
}