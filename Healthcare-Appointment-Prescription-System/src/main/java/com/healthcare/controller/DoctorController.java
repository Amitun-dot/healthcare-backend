package com.healthcare.controller;

import com.healthcare.dto.AppointmentResponse;
import com.healthcare.dto.CreatePrescriptionRequest;
import com.healthcare.dto.DoctorResponse;
import com.healthcare.dto.PrescriptionResponse;
import com.healthcare.dto.UpdateAppointmentStatusRequest;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Prescription;
import com.healthcare.entity.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;

    @GetMapping("/profile/{doctorId}")
    public DoctorResponse getProfile(@PathVariable Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .email(doctor.getUser().getEmail())
                .specialization(doctor.getSpecialization())
                .phone(doctor.getPhone())
                .experienceYears(doctor.getExperienceYears())
                .build();
    }

    @GetMapping("/profile/user/{userId}")
    public DoctorResponse getProfileByUserId(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .email(doctor.getUser().getEmail())
                .specialization(doctor.getSpecialization())
                .phone(doctor.getPhone())
                .experienceYears(doctor.getExperienceYears())
                .build();
    }

    @GetMapping("/appointments/{doctorId}")
    public List<AppointmentResponse> getAppointments(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsByDoctor(doctorId);
    }

    @PutMapping("/appointments/{appointmentId}/status")
    public com.healthcare.entity.Appointment updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestBody UpdateAppointmentStatusRequest request) {
        return appointmentService.updateAppointmentStatus(appointmentId, request);
    }

    @PostMapping("/prescriptions")
    public Prescription createPrescription(@RequestBody CreatePrescriptionRequest request) {
        return prescriptionService.createPrescription(request);
    }

    @GetMapping("/prescriptions/{doctorId}")
    public List<PrescriptionResponse> getPrescriptions(@PathVariable Long doctorId) {
        return prescriptionService.getPrescriptionsByDoctor(doctorId);
    }
}