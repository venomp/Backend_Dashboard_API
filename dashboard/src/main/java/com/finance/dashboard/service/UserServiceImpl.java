package com.finance.dashboard.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.finance.dashboard.entity.UserEntity;
import com.finance.dashboard.entity.UserRole;
import com.finance.dashboard.exception.ApiException;
import com.finance.dashboard.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity updateRole(Long userId, UserRole newRole) {
        
        UserEntity currentUser = (UserEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ApiException("Only admins can change user roles");
        }

    
        UserEntity targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));
        
        targetUser.setRole(newRole);
        return userRepository.save(targetUser);
    }
}