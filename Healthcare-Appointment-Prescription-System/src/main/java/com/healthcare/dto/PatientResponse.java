package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PatientResponse {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String gender;
    private String phone;
    private String address;
}