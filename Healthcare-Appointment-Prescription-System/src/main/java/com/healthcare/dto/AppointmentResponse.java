package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private Long id;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;
    private String status;

    private Long doctorId;
    private String doctorName;
    private String specialization;

    private Long patientId;
    private String patientName;
}