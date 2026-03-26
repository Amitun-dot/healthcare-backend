package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PrescriptionResponse {

    private Long id;

    private String diagnosis;
    private String medicines;
    private String notes;

    private Long appointmentId;
    private String appointmentDate;
    private String appointmentTime;
    private String status;

    private String doctorName;
    private String patientName;
}