package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookAppointmentRequest {

    @NotNull(message = "Doctor id is required")
    private Long doctorId;

    @NotNull(message = "Patient id is required")
    private Long patientId;

    @NotBlank(message = "Appointment date is required")
    private String appointmentDate;

    @NotBlank(message = "Appointment time is required")
    private String appointmentTime;

    @NotBlank(message = "Reason is required") // ✅ NEW
    private String reason; // ✅ NEW
}