package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DoctorResponse {

    private Long id;
    private String name;
    private String email;
    private String specialization;
    private String phone;
    private Integer experienceYears;
    private String availableFrom;
    private String availableTo;
}