package com.healthcare.controller;

import com.healthcare.dto.AppointmentResponse;
import com.healthcare.dto.BookAppointmentRequest;
import com.healthcare.dto.DoctorResponse;
import com.healthcare.dto.PatientResponse;
import com.healthcare.dto.PrescriptionResponse;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;

    @GetMapping("/profile/{patientId}")
    public PatientResponse getProfile(@PathVariable Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getUser().getName())
                .email(patient.getUser().getEmail())
                .age(patient.getAge())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .build();
    }

    @GetMapping("/profile-by-user/{userId}")
    public PatientResponse getProfileByUserId(@PathVariable Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getUser().getName())
                .email(patient.getUser().getEmail())
                .age(patient.getAge())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .build();
    }

    @GetMapping("/doctors")
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::mapDoctorResponse)
                .toList();
    }

    @PostMapping("/book-appointment")
    public com.healthcare.entity.Appointment bookAppointment(@RequestBody BookAppointmentRequest request) {
        return appointmentService.bookAppointment(request);
    }

    @GetMapping("/appointments/{patientId}")
    public List<AppointmentResponse> getAppointments(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @PutMapping("/cancel-appointment/{appointmentId}")
    public String cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return "Appointment cancelled successfully";
    }

    @GetMapping("/prescriptions/{patientId}")
    public List<PrescriptionResponse> getPrescriptions(@PathVariable Long patientId) {
        return prescriptionService.getPrescriptionsByPatient(patientId);
    }

    private DoctorResponse mapDoctorResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .email(doctor.getUser().getEmail())
                .specialization(doctor.getSpecialization())
                .phone(doctor.getPhone())
                .experienceYears(doctor.getExperienceYears())
                .availableFrom(
                        doctor.getAvailableFrom() != null ? doctor.getAvailableFrom().toString() : null
                )
                .availableTo(
                        doctor.getAvailableTo() != null ? doctor.getAvailableTo().toString() : null
                )
                .build();
    }
}