package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePrescriptionRequest {

    @NotNull(message = "Appointment id is required")
    private Long appointmentId;

    @NotNull(message = "Doctor id is required")
    private Long doctorId;

    @NotNull(message = "Patient id is required")
    private Long patientId;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    @NotBlank(message = "Medicines are required")
    private String medicines;

    @NotBlank(message = "Notes are required")
    private String notes;
}