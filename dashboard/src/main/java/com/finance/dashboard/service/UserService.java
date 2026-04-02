package com.finance.dashboard.service;

import com.finance.dashboard.entity.UserEntity;
import com.finance.dashboard.entity.UserRole;

public interface UserService {
    

    UserEntity updateRole(Long userId, UserRole newRole);
    
}
