package com.healthcare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {   // ✅ MUST be public
    private Long id;
    private String name;
    private String email;
    private String role;
}