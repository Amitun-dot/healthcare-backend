package com.healthcare.controller;

import com.healthcare.dto.AppointmentResponse;
import com.healthcare.dto.BookAppointmentRequest;
import com.healthcare.dto.UpdateAppointmentStatusRequest;
import com.healthcare.entity.Appointment;
import com.healthcare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public Appointment bookAppointment(@Valid @RequestBody BookAppointmentRequest request) {
        return appointmentService.bookAppointment(request);
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponse> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentResponse> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsByDoctor(doctorId);
    }

    @PutMapping("/{appointmentId}/status")
    public Appointment updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {
        return appointmentService.updateAppointmentStatus(appointmentId, request);
    }

    @DeleteMapping("/{appointmentId}")
    public String deleteAppointment(@PathVariable Long appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return "Appointment deleted successfully";
    }
}