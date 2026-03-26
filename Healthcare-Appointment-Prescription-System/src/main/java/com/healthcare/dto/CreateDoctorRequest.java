package com.healthcare.dto;

import lombok.Data;

@Data
public class CreateDoctorRequest {

    private String name;
    private String email;
    private String password;

    private String specialization;
    private String phone;
    private Integer experienceYears;
}