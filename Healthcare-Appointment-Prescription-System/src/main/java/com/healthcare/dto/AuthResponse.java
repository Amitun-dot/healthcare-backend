package com.healthcare.dto;

import com.healthcare.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private String token;
}