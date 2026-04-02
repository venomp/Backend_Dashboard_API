package com.finance.dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.dashboard.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
    
}
