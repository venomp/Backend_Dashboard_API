package com.finance.dashboard.dto;

import com.finance.dashboard.entity.UserRole;
import jakarta.validation.constraints.NotNull;

public class RoleUpdateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "New role must be specified")
    private UserRole newRole;

    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserRole getNewRole() {
        return newRole;
    }

    public void setNewRole(UserRole newRole) {
        this.newRole = newRole;
    }
}