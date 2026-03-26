package com.healthcare.service;

import com.healthcare.dto.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    void deleteUser(Long userId);
}