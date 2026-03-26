package com.healthcare.service.impl;

import com.healthcare.dto.AppointmentResponse;
import com.healthcare.dto.BookAppointmentRequest;
import com.healthcare.dto.UpdateAppointmentStatusRequest;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentStatus;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.entity.Prescription;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    public Appointment bookAppointment(BookAppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDate appointmentDate = LocalDate.parse(request.getAppointmentDate());
        LocalTime appointmentTime = LocalTime.parse(request.getAppointmentTime());

        // Check doctor availability time
        if (appointmentTime.isBefore(doctor.getAvailableFrom()) ||
                appointmentTime.isAfter(doctor.getAvailableTo())) {
            throw new RuntimeException("Doctor is not available at this time");
        }

        // Prevent duplicate slot booking for same doctor/date/time
        boolean alreadyBooked = appointmentRepository
                .existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        doctor,
                        appointmentDate,
                        appointmentTime,
                        AppointmentStatus.CANCELLED
                );

        if (alreadyBooked) {
            throw new RuntimeException("This time slot is already booked");
        }

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .reason(request.getReason())
                .status(AppointmentStatus.PENDING)
                .build();

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return appointmentRepository.findByPatient(patient)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return appointmentRepository.findByDoctor(doctor)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Appointment updateAppointmentStatus(Long appointmentId, UpdateAppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(request.getStatus());
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Optional<Prescription> prescription = prescriptionRepository.findByAppointment(appointment);
        prescription.ifPresent(prescriptionRepository::delete);

        appointmentRepository.delete(appointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .appointmentDate(appointment.getAppointmentDate().toString())
                .appointmentTime(appointment.getAppointmentTime().toString())
                .reason(appointment.getReason())
                .status(appointment.getStatus().name())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUser().getName())
                .specialization(appointment.getDoctor().getSpecialization())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getUser().getName())
                .build();
    }
}